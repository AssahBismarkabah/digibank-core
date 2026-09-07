#!/usr/bin/env bash
# =============================================================================
# Digi Bank -- Newman (Postman) DAST replay runner
#
# Versioned, repeatable entry point for replaying the Digi Bank DAST Postman
# collection with Newman, producing CLI/JSON/HTML reports under dast/reports/.
#
# The collection is environment-parametrised (baseUrl, auth token, seeded ids)
# so the same scenarios can be replayed against local, dev and prod targets.
#
# Usage:
#   ./dast/postman/newman-run.sh                     # default: local
#   ./dast/postman/newman-run.sh --env dev
#   ./dast/postman/newman-run.sh --env prod --informational
#   ENV=dev ./dast/postman/newman-run.sh
#
# Options:
#   --env <local|dev|prod>   Which environment JSON to use (default: local).
#   --informational          Exit 0 even if assertions fail (non-blocking CI
#                            posture until the hardening tickets land).
# =============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
COLLECTION="$SCRIPT_DIR/DigiBank-DAST-Validation.postman_collection.json"
REPORTS_DIR="$PROJECT_ROOT/dast/reports"

ENV_NAME="local"
INFORMATIONAL=0

# --- arg parsing ------------------------------------------------------------

while [ $# -gt 0 ]; do
    case "$1" in
        --env)
            ENV_NAME="$2"
            shift 2
            ;;
        --informational)
            INFORMATIONAL=1
            shift
            ;;
        *)
            echo "Unknown argument: $1" >&2
            exit 2
            ;;
    esac
done

case "$ENV_NAME" in
    local) ENVIRONMENT="$SCRIPT_DIR/DigiBank-local.postman_environment.json" ;;
    dev)   ENVIRONMENT="$SCRIPT_DIR/DigiBank-dev.postman_environment.json"   ;;
    prod)  ENVIRONMENT="$SCRIPT_DIR/DigiBank-prod.postman_environment.json"  ;;
    *)
        echo "Unknown environment: '$ENV_NAME' (expected local, dev or prod)" >&2
        exit 2
        ;;
esac

# --- newman availability ----------------------------------------------------

if ! command -v npx >/dev/null 2>&1; then
    echo "  [ERROR] Node.js/npx not found. Install Node.js, then: npm install -g newman newman-reporter-html" >&2
    exit 1
fi

# --- run --------------------------------------------------------------------

RUN_TS="$(date +%Y%m%d-%H%M%S)"
RUN_DIR="$REPORTS_DIR/$RUN_TS"
mkdir -p "$RUN_DIR"

echo "  [INFO]  Replaying DAST collection against environment: $ENV_NAME"
echo "  [INFO]  Environment file: $ENVIRONMENT"
echo "  [INFO]  Reports: $RUN_DIR"

# The HTML reporter ships as a separate package (newman-reporter-html). When it
# is not installed we fall back to CLI + JSON only so the replay never breaks.
REPORTERS="cli,json"
HTML_EXPORT=""
NODE_PATH_REPORTER=""
html_reporter_available() {
    [ -d "node_modules/newman-reporter-html" ] && return 0
    ls "$(npm root -g)/newman-reporter-html" >/dev/null 2>&1 && return 0
    [ -n "${NODE_PATH:-}" ] && ls "$NODE_PATH/newman-reporter-html" >/dev/null 2>&1
    return $?
}
if html_reporter_available; then
    REPORTERS="cli,json,html"
    HTML_EXPORT="--reporter-html-export $RUN_DIR/newman-report.html"
    if [ -n "${NODE_PATH:-}" ] && ls "$NODE_PATH/newman-reporter-html" >/dev/null 2>&1; then
        NODE_PATH_REPORTER="$NODE_PATH"
    fi
fi

# Capture Newman's exit code and continue so reports/artefacts are still written.
RC=0
NODE_PATH="${NODE_PATH_REPORTER:-}${NODE_PATH:+:$NODE_PATH}" \
npx --yes newman run "$COLLECTION" \
    --environment "$ENVIRONMENT" \
    --reporters "$REPORTERS" \
    --reporter-json-export "$RUN_DIR/newman-report.json" $HTML_EXPORT \
    || RC=$?

# Keep a stable "latest" report path for easy tooling/human consumption.
rm -rf "$REPORTS_DIR/latest"
ln -s "$RUN_TS" "$REPORTS_DIR/latest"

if [ -n "$HTML_EXPORT" ]; then
    echo "  [INFO]  HTML report written to $RUN_DIR/newman-report.html"
fi
echo "  [INFO]  JSON report written to $RUN_DIR/newman-report.json"

if [ "$INFORMATIONAL" -eq 1 ]; then
    echo "  [INFO]  Informational mode: assertion failures do not fail the run."
    exit 0
fi

exit "$RC"
