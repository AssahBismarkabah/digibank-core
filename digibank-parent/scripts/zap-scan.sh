#!/usr/bin/env bash
# =============================================================================
# DigiBank – OWASP ZAP Scan Script
#
# Runs OWASP ZAP (via Docker) against a running DigiBank instance and saves
# HTML, JSON, and XML reports to dast/reports/.
#
# Usage
# -----
#   ./scripts/zap-scan.sh                          # baseline scan, localhost:8080
#   ./scripts/zap-scan.sh --mode full              # full active scan (slower)
#   ./scripts/zap-scan.sh --target http://host:9090
#   BASE_URL=http://host:9090 ./scripts/zap-scan.sh
#
# Options
#   --mode baseline|full   Scan mode (default: baseline)
#   --target <url>         Override the target URL
#   --help                 Show this help
#
# Output
#   dast/reports/zap-report.html
#   dast/reports/zap-report.json
#   dast/reports/zap-report.xml
#
# Requirements
#   docker  (Docker Engine or Docker Desktop)
#
# Notes
#   * Alert-based findings do not block the workflow (informational until a scan
#     policy is agreed). Change ZAP_EXIT_CODE below to exit "$ZAP_RAW_EXIT" to
#     make failing alerts blocking. Scan-process failures (docker/image/startup
#     errors) always fail the script.
#   * The scan policy dast/zap/zap-scan-policy.xml is used only in full mode.
#     Baseline mode uses ZAP's built-in passive-scan ruleset.
# =============================================================================

set -euo pipefail

# ── defaults -----------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DAST_DIR="$REPO_ROOT/../dast"
REPORTS_DIR="$DAST_DIR/reports"
ZAP_DIR="$DAST_DIR/zap"

TARGET="${BASE_URL:-http://localhost:8080}"
SCAN_MODE="baseline"
ZAP_IMAGE="ghcr.io/zaproxy/zaproxy:stable"

# ── argument parsing ---------------------------------------------------------
while [[ $# -gt 0 ]]; do
  case "$1" in
    --mode)
      SCAN_MODE="$2"; shift 2 ;;
    --target)
      TARGET="$2"; shift 2 ;;
    --help|-h)
      grep '^#' "$0" | sed 's/^# \?//' | head -40
      exit 0 ;;
    *)
      echo "[ZAP] Unknown option: $1" >&2; exit 1 ;;
  esac
done

if [[ "$SCAN_MODE" != "baseline" && "$SCAN_MODE" != "full" ]]; then
  echo "[ZAP] --mode must be 'baseline' or 'full'" >&2
  exit 1
fi

# ── helpers ------------------------------------------------------------------
info()  { printf "  [ZAP]  %s\n" "$*"; }
ok()    { printf "  [OK]   %s\n" "$*"; }
warn()  { printf "  [WARN] %s\n" "$*"; }

# ── preflight: check docker --------------------------------------------------
if ! command -v docker &>/dev/null; then
  echo "[ZAP] ERROR: docker is not installed or not in PATH." >&2
  exit 1
fi

# ── create reports directory -------------------------------------------------
mkdir -p "$REPORTS_DIR"
info "Reports will be written to: $REPORTS_DIR"

# ── wait for application -------------------------------------------------
info "Checking that DigiBank is reachable at $TARGET ..."
READY=0
for i in $(seq 1 12); do
  # resolve host.docker.internal → localhost for the pre-flight check
  CHECK_URL="${TARGET/host.docker.internal/localhost}"
  if curl --silent --fail --connect-timeout 3 --max-time 5 \
      "${CHECK_URL}/" > /dev/null 2>&1 || \
     curl --silent --fail --connect-timeout 3 --max-time 5 \
      "${CHECK_URL}/api/customers" > /dev/null 2>&1; then
    READY=1
    break
  fi
  info "Waiting for application... ($i/12)"
  sleep 5
done

if [[ "$READY" -eq 0 ]]; then
  warn "Application did not respond at $TARGET after 60 s."
  warn "Start the dev stack first:  cd digibank-parent && make up"
  exit 1
fi
ok "Application is reachable."

# ── pull the image -----------------------------------------------------------
info "Pulling ZAP image: $ZAP_IMAGE"
docker pull --quiet "$ZAP_IMAGE" || warn "Could not pull latest ZAP image – using cached version."

# ── build the docker run command ---------------------------------------------
# Mount dast/ directory so ZAP can read the context/policy and write reports.
# host.docker.internal resolves to the host from inside the container on
# Docker Desktop (Linux/macOS/Windows). On Linux Docker Engine it resolves via
# --add-host=host.docker.internal:host-gateway.
DOCKER_EXTRA_HOSTS=("--add-host=host.docker.internal:host-gateway")

COMMON_ARGS=(
  "--rm"
  "-v" "${DAST_DIR}:/zap/wrk:rw"
  "${DOCKER_EXTRA_HOSTS[@]}"
  "$ZAP_IMAGE"
)

if [[ "$SCAN_MODE" == "baseline" ]]; then
  info "Starting BASELINE scan against $TARGET ..."
  info "(passive spider + passive scan rules – informational only)"
  ZAP_CMD=(
    "zap-baseline.py"
    "-t" "$TARGET"
    "-J" "/zap/wrk/reports/zap-report.json"
    "-r" "/zap/wrk/reports/zap-report.html"
    "-x" "/zap/wrk/reports/zap-report.xml"
    "-I"          # do not return failure code for alerts
    "-d"          # debug output
  )
else
  info "Starting FULL active scan against $TARGET ..."
  warn "Full scan sends active probes – only run against test/dev environments!"
  ZAP_CMD=(
    "zap-full-scan.py"
    "-t" "$TARGET"
    "-c" "/zap/wrk/zap/zap-context.xml"
    "-z" "activeScan.policyname=digibank-baseline"
    "-J" "/zap/wrk/reports/zap-report.json"
    "-r" "/zap/wrk/reports/zap-report.html"
    "-x" "/zap/wrk/reports/zap-report.xml"
    "-I"          # do not return failure code for alerts
    "-d"          # debug output
  )
fi

# ── run ZAP -----------------------------------------------------------------
set +e  # allow ZAP to exit non-zero (alerts present or scan failure) without aborting
docker run "${COMMON_ARGS[@]}" "${ZAP_CMD[@]}"
ZAP_RAW_EXIT=$?
set -e

# Distinguish "scan completed" from "scan failed":
#   * `-I` makes ZAP return 0 for alert-based findings, so a non-zero exit here
#     means the scan process itself failed to run (docker unavailable, image or
#     container failed to start, ZAP errored out) and must be surfaced.
#   * Alert-based findings remain informational until a scan policy is agreed.
# Change ZAP_EXIT_CODE below to exit "$ZAP_RAW_EXIT" to make failing alerts
# block the script once a scan policy is agreed.
if [[ "$ZAP_RAW_EXIT" -ne 0 ]]; then
  warn "ZAP scan process failed (exit code $ZAP_RAW_EXIT)."
  ZAP_EXIT_CODE="$ZAP_RAW_EXIT"
else
  ZAP_EXIT_CODE=0
fi

# ── summary -----------------------------------------------------------------
echo ""
echo "=================================================="
echo "  ZAP scan complete"
echo "  Mode    : $SCAN_MODE"
echo "  Target  : $TARGET"
echo "  Reports :"
for f in "$REPORTS_DIR"/zap-report.{html,json,xml}; do
  [[ -f "$f" ]] && echo "    $f"
done
echo "  Raw ZAP exit code: $ZAP_RAW_EXIT (non-zero = scan process failed; -I keeps alert findings from blocking)"
echo "  Script exit code : $ZAP_EXIT_CODE (informational mode for alerts; hard failure on scan errors)"
echo "=================================================="
echo ""
info "To open the HTML report:"
info "  xdg-open $REPORTS_DIR/zap-report.html"
info "  # or on macOS: open $REPORTS_DIR/zap-report.html"

exit $ZAP_EXIT_CODE
