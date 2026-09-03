# DigiBank DAST – OWASP ZAP

This directory contains all artefacts for the Dynamic Application Security Testing (DAST)
campaign described in the Workshop 3 guide (§2.13-2.15, §4.9).

---

## Running ZAP locally

> **Prerequisite**: the DigiBank dev stack must be running on port 8080.
>
> ```bash
> cd digibank-parent
> make up            # starts postgres + app (dev profile)
> ```

### Option 1 – Makefile shortcut (recommended)

```bash
cd digibank-parent
make zap-scan      # runs ZAP baseline scan; reports land in dast/reports/
make zap-report    # opens the HTML report in the default browser
```

### Option 2 – Script directly

```bash
cd digibank-parent
./scripts/zap-scan.sh                        # baseline (passive + spider)
./scripts/zap-scan.sh --mode full            # full active scan (slower)
./scripts/zap-scan.sh --target http://localhost:9090   # custom target
```

### Option 3 – Docker Compose ZAP profile

```bash
cd digibank-parent
docker compose --profile dev --profile zap up --build -d
docker compose logs -f zap
```

The ZAP container targets `http://digibank-app:8080` (the internal network alias)
and writes reports into `dast/reports/` via a bind-mount.

---

## Running Newman (Postman collection replay)

Newman replays the DAST-oriented HTTP scenarios and verifies response envelopes.

> **Note on pre-hardening assertions.** The security-header assertions
> (test group 4.1) are expected to fail until the hardening tickets add the
> missing response headers, and the Swagger check (test 4.2) is skipped until
> the springdoc/OpenAPI dependency lands. These are documented baselines, not
> regressions.

```bash
# one-off
npx newman run dast/postman/DigiBank-DAST-Validation.postman_collection.json \
  --environment dast/postman/DigiBank-local.postman_environment.json \
  --reporters cli

# with HTML + JSON reports
mkdir -p dast/reports
npx newman run dast/postman/DigiBank-DAST-Validation.postman_collection.json \
  --environment dast/postman/DigiBank-local.postman_environment.json \
  --reporters cli,json,html \
  --reporter-json-export dast/reports/newman-report.json \
  --reporter-html-export dast/reports/newman-report.html
```

---

## CI integration

Two jobs are added to `.github/workflows/ci.yml`:

| Job | Tool | When |
|-----|------|------|
| `dast-newman` | Newman | After `build-and-test` |
| `dast-zap`    | OWASP ZAP baseline | After `dast-newman` |

Both jobs upload their reports as GitHub Actions artefacts.
By default, findings are **informational** (the ZAP baseline runs with
`fail_action: false` and the Newman step is non-blocking). To make ZAP alert
findings fail the job without editing the workflow, set the repository variable
`DAST_BLOCKING` to `true`:

```
Settings → Secrets and variables → Actions → Variables → DAST_BLOCKING = true
```

The intent is to flip the DAST jobs to blocking permanently once the hardening
tickets are merged.

---

## Scan policy

`dast/zap/zap-scan-policy.xml` enables the following scan rules (LOW strength,
LOW threshold – appropriate for a baseline):

| Category | Examples |
|----------|---------|
| Missing security headers | X-Frame-Options, CSP, HSTS, X-Content-Type-Options |
| Application error disclosure | Stack traces, Java exception class names |
| Server information leakage | `Server:` banner, `X-Powered-By` |
| Path / parameter injection | Basic SQL injection, path traversal probes |

---

## Before / after comparison

The `dast/reports/baseline/` directory holds the **pre-hardening** findings.
After each hardening ticket is merged, re-run `make zap-scan` and compare
the new report against the baseline to demonstrate remediation effectiveness.
