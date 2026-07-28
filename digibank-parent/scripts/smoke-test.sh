#!/usr/bin/env bash
# =============================================================================
# Digi Bank -- Smoke Test Script
#
# Verifies that the Digi Bank REST API is operational by exercising the
# core CRUD endpoints for customers, accounts, transactions, and compliance.
#
# Usage:
#   ./scripts/smoke-test.sh                    # default: http://localhost:8080
#   BASE_URL=http://localhost:9090 ./scripts/smoke-test.sh
# =============================================================================

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
PASS=0
FAIL=0
RUN_ID="${RUN_ID:-$(date +%s)-$$}"
TEST_ACCOUNT_NUMBER="${TEST_ACCOUNT_NUMBER:-SMK-${RUN_ID:0:16}}"

# --- helpers ----------------------------------------------------------------

info()  { printf "  [INFO]  %s\n" "$*"; }
ok()    { printf "  [PASS]  %s\n" "$*"; PASS=$((PASS + 1)); }
fail()  { printf "  [FAIL]  %s\n" "$*"; FAIL=$((FAIL + 1)); }

check_status() {
    local expected="$1" actual="$2" label="$3"
    if [ "$actual" -eq "$expected" ]; then
        ok "$label"
    else
        fail "$label (expected HTTP $expected, got $actual)"
    fi
}

http_status() {
    curl -s -o /dev/null -w "%{http_code}" "$@" || true
}

http_body_status() {
    curl -s -w "\n%{http_code}" "$@" || true
}

extract_id() {
    sed -n 's/.*"id"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*/\1/p'
}

# --- fresh database ---------------------------------------------------------
# Restart the app container so Hibernate ddl-auto:create gives us clean tables.
# This makes the smoke test idempotent -- safe to run multiple times.
APP_CONTAINER=""
if command -v docker &>/dev/null; then
    if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^digibank-app$'; then
        APP_CONTAINER="digibank-app"
    elif docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^digibank-wildfly$'; then
        APP_CONTAINER="digibank-wildfly"
    fi
fi
if [ -n "$APP_CONTAINER" ]; then
    info "Restarting $APP_CONTAINER for a fresh database..."
    docker restart "$APP_CONTAINER" >/dev/null
fi

# --- context path detection & wait for availability -------------------------
# WildFly deploys the WAR at /digibank-app context path; embedded Tomcat uses /.
# Probe both during the wait loop and set CONTEXT based on whichever responds.
CONTEXT=""
API=""
READY=0
for i in $(seq 1 120); do
    if curl -sf "$BASE_URL/api/customers" >/dev/null 2>&1; then
        CONTEXT=""
        API="$BASE_URL"
        READY=1
        sleep 2  # extra settle time for Hibernate to finish schema creation
        break
    elif curl -sf "$BASE_URL/digibank-app/api/customers" >/dev/null 2>&1; then
        CONTEXT="/digibank-app"
        API="$BASE_URL$CONTEXT"
        READY=1
        sleep 2
        break
    fi
    sleep 1
done

if [ "$READY" -eq 0 ]; then
    printf "  [FAIL] Timed out waiting for application to respond at %s or %s/digibank-app\n" "$BASE_URL" "$BASE_URL"
    exit 1
fi

# --- health check -----------------------------------------------------------

info "Smoke testing Digi Bank at $API"
echo ""

# --- 1. Customer endpoints --------------------------------------------------

info "=== Customer Endpoints ==="

# GET /api/customers
status=$(http_status "$API/api/customers")
check_status 200 "$status" "GET /api/customers"

# POST /api/customers (create)
body=$(http_body_status \
    -X POST "$API/api/customers" \
    -H "Content-Type: application/json" \
    -d "{\"firstName\":\"Smoke\",\"lastName\":\"Customer\",\"email\":\"smoke.customer.$RUN_ID@test.com\"}")
status=$(printf "%s" "$body" | tail -n1)
CUSTOMER_ID=$(printf "%s" "$body" | sed '$d' | extract_id)
check_status 201 "$status" "POST /api/customers (create smoke customer)"

# GET /api/customers/{id}
status=$(http_status "$API/api/customers/$CUSTOMER_ID")
check_status 200 "$status" "GET /api/customers/{id}"

# PUT /api/customers/{id}
status=$(http_status \
    -X PUT "$API/api/customers/$CUSTOMER_ID" \
    -H "Content-Type: application/json" \
    -d "{\"firstName\":\"Smoke\",\"lastName\":\"Updated\",\"email\":\"smoke.customer.$RUN_ID@test.com\"}")
check_status 200 "$status" "PUT /api/customers/{id} (update)"

# DELETE /api/customers/{id}
status=$(http_status -X DELETE "$API/api/customers/$CUSTOMER_ID")
check_status 204 "$status" "DELETE /api/customers/{id}"

echo ""

# --- 2. Account endpoints ---------------------------------------------------

info "=== Account Endpoints ==="

# GET /api/accounts
status=$(http_status "$API/api/accounts")
check_status 200 "$status" "GET /api/accounts"

# POST /api/accounts (create)
body=$(http_body_status \
    -X POST "$API/api/accounts" \
    -H "Content-Type: application/json" \
    -d "{\"accountNumber\":\"$TEST_ACCOUNT_NUMBER\",\"balance\":1000.00,\"customerId\":1,\"accountType\":\"CHECKING\",\"currency\":\"EUR\"}")
status=$(printf "%s" "$body" | tail -n1)
ACCOUNT_ID=$(printf "%s" "$body" | sed '$d' | extract_id)
check_status 201 "$status" "POST /api/accounts (create smoke account)"

# GET /api/accounts/{id}
status=$(http_status "$API/api/accounts/$ACCOUNT_ID")
check_status 200 "$status" "GET /api/accounts/{id}"

# PUT /api/accounts/{id}
status=$(http_status \
    -X PUT "$API/api/accounts/$ACCOUNT_ID" \
    -H "Content-Type: application/json" \
    -d "{\"accountNumber\":\"$TEST_ACCOUNT_NUMBER\",\"balance\":1250.00,\"customerId\":1,\"accountType\":\"SAVINGS\",\"currency\":\"EUR\"}")
check_status 200 "$status" "PUT /api/accounts/{id} (update)"

# GET /api/accounts/by-customer/{customerId}
status=$(http_status "$API/api/accounts/by-customer/1")
check_status 200 "$status" "GET /api/accounts/by-customer/{customerId}"

echo ""

# --- 3. Transaction endpoints -----------------------------------------------

info "=== Transaction Endpoints ==="

# GET /api/transactions
status=$(http_status "$API/api/transactions")
check_status 200 "$status" "GET /api/transactions"

# POST /api/transactions (create)
body=$(http_body_status \
    -X POST "$API/api/transactions" \
    -H "Content-Type: application/json" \
    -d "{\"accountId\":$ACCOUNT_ID,\"amount\":250.00,\"transactionType\":\"DEPOSIT\",\"description\":\"Smoke test deposit\"}")
status=$(printf "%s" "$body" | tail -n1)
TRANSACTION_ID=$(printf "%s" "$body" | sed '$d' | extract_id)
check_status 201 "$status" "POST /api/transactions (create deposit)"

# GET /api/transactions/{id}
status=$(http_status "$API/api/transactions/$TRANSACTION_ID")
check_status 200 "$status" "GET /api/transactions/{id}"

# PUT /api/transactions/{id}
status=$(http_status \
    -X PUT "$API/api/transactions/$TRANSACTION_ID" \
    -H "Content-Type: application/json" \
    -d "{\"accountId\":$ACCOUNT_ID,\"amount\":300.00,\"transactionType\":\"DEPOSIT\",\"description\":\"Smoke test deposit updated\"}")
check_status 200 "$status" "PUT /api/transactions/{id} (update)"

# GET /api/transactions/by-account/{accountId}
status=$(http_status "$API/api/transactions/by-account/$ACCOUNT_ID")
check_status 200 "$status" "GET /api/transactions/by-account/{accountId}"

# DELETE /api/transactions/{id}
status=$(http_status -X DELETE "$API/api/transactions/$TRANSACTION_ID")
check_status 204 "$status" "DELETE /api/transactions/{id}"

echo ""

# --- 4. Compliance endpoints ------------------------------------------------

info "=== Compliance Endpoints ==="

# GET /api/compliance
status=$(http_status "$API/api/compliance")
check_status 200 "$status" "GET /api/compliance"

# POST /api/compliance (create)
body=$(http_body_status \
    -X POST "$API/api/compliance" \
    -H "Content-Type: application/json" \
    -d '{"customerId":1,"checkType":"KYC","status":"PASSED","checkedBy":"SYSTEM","remarks":"Smoke test KYC check"}')
status=$(printf "%s" "$body" | tail -n1)
COMPLIANCE_ID=$(printf "%s" "$body" | sed '$d' | extract_id)
check_status 201 "$status" "POST /api/compliance (create KYC check)"

# GET /api/compliance/{id}
status=$(http_status "$API/api/compliance/$COMPLIANCE_ID")
check_status 200 "$status" "GET /api/compliance/{id}"

# PUT /api/compliance/{id}
status=$(http_status \
    -X PUT "$API/api/compliance/$COMPLIANCE_ID" \
    -H "Content-Type: application/json" \
    -d '{"customerId":1,"checkType":"AML","status":"REVIEW","checkedBy":"SYSTEM","remarks":"Smoke test AML review"}')
check_status 200 "$status" "PUT /api/compliance/{id} (update)"

# GET /api/compliance/by-customer/{customerId}
status=$(http_status "$API/api/compliance/by-customer/1")
check_status 200 "$status" "GET /api/compliance/by-customer/{customerId}"

# DELETE /api/compliance/{id}
status=$(http_status -X DELETE "$API/api/compliance/$COMPLIANCE_ID")
check_status 204 "$status" "DELETE /api/compliance/{id}"

echo ""

# --- 5. Account cleanup endpoint -------------------------------------------

info "=== Account Cleanup Endpoint ==="

# DELETE /api/accounts/{id}
status=$(http_status -X DELETE "$API/api/accounts/$ACCOUNT_ID")
check_status 204 "$status" "DELETE /api/accounts/{id}"

echo ""

# --- 6. Index page ----------------------------------------------------------

info "=== Index Page ==="

status=$(http_status "$API/")
check_status 200 "$status" "GET / (index page)"

echo ""

# --- summary ----------------------------------------------------------------

echo "=========================================="
printf "  Results: %d passed, %d failed\n" "$PASS" "$FAIL"
echo "=========================================="

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
