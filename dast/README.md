# DigiBank DAST – OWASP ZAP

This directory contains all artefacts for the Dynamic Application Security Testing (DAST)
campaign described in the Workshop 3 guide (§2.13-2.15, §4.9).

---

## Directory layout

```
dast/
├── README.md                   ← you are here
├── postman/
│   ├── DigiBank-DAST-Validation.postman_collection.json
│   └── DigiBank-local.postman_environment.json
├── reports/
│   ├── baseline/               ← committed baseline from the first scan
│   │   └── README.md
│   └── .gitkeep
└── zap/
    ├── zap-context.xml         ← ZAP context (target URL + scope)
    └── zap-scan-policy.xml     ← active-scan policy (headers / error disclosure / exposure)
```

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
`fail_action: false` is set on the ZAP job so findings are **informational** until
a scan policy is agreed (see ticket acceptance criteria).

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
