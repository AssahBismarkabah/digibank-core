# DigiBank DAST – OWASP ZAP

This directory contains all artefacts for the Dynamic Application Security Testing (DAST)
campaign described in the Workshop 3 guide (§2.13-2.15, §4.9).

---

## Running ZAP locally

> **Prerequisite**: the DigiBank microservice stack must be running on port 8080.
>
> ```bash
> make up            # starts PostgreSQL and the microservice stack
> ```

### Option 1 – Makefile shortcut (recommended)

```bash
make zap-scan      # runs the ZAP baseline scan; reports land in dast/reports/
```

### Option 2 – Script directly

```bash
docker run --rm --network host \
  -v "$PWD/dast/reports:/zap/wrk:rw" \
  zaproxy/zap-stable zap-baseline.py \
  -t http://127.0.0.1:8080/ -r zap-report.html -J zap-report.json -w zap-report.md
```

### Option 3 – Docker Compose ZAP profile

The local Makefile target and the CI job both scan the gateway at
`http://127.0.0.1:8080/`. This keeps the scan boundary stable while additional
microservices are added behind the gateway.

---

## Running Newman (Postman collection replay)

Newman replays the DAST-oriented HTTP scenarios and verifies response envelopes
(headlessly, no GUI). The collection covers **customers, accounts, transactions
and compliance** nominal and invalid scenarios, and asserts that error
responses stay generic (no raw ids / class names / stack traces leaked).

### Collection & environments

Three environment files share the same key set (`baseUrl`, seeded ids,
`authToken`) but differ only in the target base URL:

| Environment | File | baseUrl |
|-------------|------|---------|
| local | `DigiBank-local.postman_environment.json` | `http://127.0.0.1:8080` |
| dev   | `DigiBank-dev.postman_environment.json`   | `https://dev.digibank.internal` |
| prod  | `DigiBank-prod.postman_environment.json`  | `https://api.digibank.example` |

`authToken` is an optional, disabled-by-default placeholder for when an auth
mechanism is added; none is required today.

### Versioned runner (recommended)

`dast/postman/newman-run.sh` wraps Newman so the replay is repeatable across
environments and produces timestamped CLI/JSON/HTML reports under
`dast/reports/<timestamp>/` (with a `dast/reports/latest` symlink).

```bash
make newman-scan                            # replay local through the gateway
./dast/postman/newman-run.sh              # replay local (default)
./dast/postman/newman-run.sh --env dev    # replay dev target
./dast/postman/newman-run.sh --env prod --informational
```

`--informational` exits `0` even if assertions fail (the non-blocking CI
posture until the hardening tickets land). Without it, the script exits with
Newman's pass/fail status.

### One-off Newman commands

```bash
# one-off, CLI only
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

> **Note on baseline assertions.** A few assertions document *expected*
> baseline differences and are not regressions:
>
> * Security-header assertions (group 4.1) verify the gateway response headers
>   and should pass for the current microservice stack.
> * The Swagger check (4.2) is skipped until the springdoc/OpenAPI dependency
>   lands.
> * The `404 does not echo raw probe id` assertions (groups 3.x and 5.7)
>   currently fail because the app echoes the requested id in the not-found
>   message (e.g. `Compliance check not found with id: <id>`). They will pass
>   once the hardening work replaces those messages with generic text.
> * Malformed JSON (2.6) is asserted to return **500** with the generic error
>   envelope — the app has no dedicated `HttpMessageNotReadableException`
>   handler today, so this is the real, verified behaviour.

---

## Live smoke test & verification

A "live test" replays the collection against a **real running instance** of
DigiBank and shows a pass/fail report, rather than just validating the JSON
files. Use it to prove the collection, environments and runner all work
end-to-end.

### Prerequisites

* The DigiBank microservice stack is running: `make up`
* Newman is available: `npm install -g newman` (the HTML reporter is optional;
  the runner degrades to CLI + JSON without it)
* No other service occupies port `8080` on the host

> **Port conflict.** The container binds `8080:8080` in `docker-compose.yml`.
> If `8080` is already used by another process (e.g. a stray container), you
> will see *"Bind for 0.0.0.0:8080 failed: port is already allocated"* when the
> stack starts. Stop the conflicting container, or run the app on a free host
> port (see below) before replaying.

### Step 1 – start the stack

```bash
make up                 # starts PostgreSQL and the gateway (port 8080)
```

Wait until the app answers:

```bash
curl -sf http://127.0.0.1:8080/api/customers && echo "OK"
```

### Step 2 – run the live replay

Use the versioned runner (recommended):

```bash
./dast/postman/newman-run.sh --env local           # blocking: exits 1 on failed assertions
./dast/postman/newman-run.sh --env local --informational   # always exits 0
```

Or Newman directly:

```bash
npx newman run dast/postman/DigiBank-DAST-Validation.postman_collection.json \
  --environment dast/postman/DigiBank-local.postman_environment.json \
  --reporters cli
```

Reports are written to `dast/reports/<timestamp>/newman-report.{json,html}` and
`dast/reports/latest` points at the newest run.

### Step 3 – interpret the report

* **Nominal scenarios must pass:** customer/account/transaction/compliance
  create, list, get-by-id, and the 404 probes return the expected status codes
  with no class-name / stack-trace / SQL leakage.
* **Invalid scenarios must be rejected:** empty fields, negative/zero amounts,
  and malformed JSON return the documented error envelopes.
* The security-header assertion is expected to pass on the current gateway.
  Treat unexpected failures as implementation or environment regressions.

### Running against an alternate port (when 8080 is taken)

Build the image once, then start the app container on a free host port and
point an environment file at it:

```bash
docker compose -f digibank-microservices/docker-compose.yml build api-gateway
docker run -d --name digibank-gateway-test \
  --network digibank-microservices_digibank-net \
  -p 8090:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/digibank_db \
  -e SPRING_DATASOURCE_USERNAME=digibank_user \
  -e SPRING_DATASOURCE_PASSWORD=digibank_pwd \
  digibank-microservices-api-gateway:latest
```

Then create a throwaway environment that targets the alternate port and replay:

```bash
cp dast/postman/DigiBank-local.postman_environment.json /tmp/digibank-local-8090.json
# edit baseUrl in /tmp/digibank-local-8090.json to http://127.0.0.1:8090
npx newman run dast/postman/DigiBank-DAST-Validation.postman_collection.json \
  --environment /tmp/digibank-local-8090.json --reporters cli
# cleanup
docker rm -f digibank-app-test
```

---

## CI integration

The microservice workflow contains two security jobs in `.github/workflows/ci.yml`:

| Job | Tool | When |
|-----|------|------|
| `dast-newman` | Newman | After `build-test-smoke` |
| `dast-zap`    | OWASP ZAP baseline | After `dast-newman` |

Both jobs upload their reports as GitHub Actions artefacts.
By default, findings are **informational** while the baseline is being
established. Newman uses informational mode and ZAP runs with `fail_action:
false`. Promote findings to blocking only after the corresponding remediation
tickets are agreed and the workflow policy is changed deliberately.

```
The build, container smoke test, Newman replay, and ZAP baseline are separate
jobs so future security scanners can be added without mixing their reports or
their failure policies.

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

The `dast/reports/baseline/` directory holds the initial reference findings.
After each hardening change, re-run `make zap-scan` and compare the new report
against the baseline to demonstrate remediation effectiveness.
