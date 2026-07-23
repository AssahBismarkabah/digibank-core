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

# --- health check -----------------------------------------------------------

info "Smoke testing Digi Bank at $BASE_URL"
echo ""

# --- 1. Customer endpoints --------------------------------------------------

info "=== Customer Endpoints ==="

# GET /api/customers (empty list)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/customers")
check_status 200 "$status" "GET /api/customers (empty)"

# POST /api/customers (create)
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/api/customers" \
    -H "Content-Type: application/json" \
    -d '{"firstName":"Alice","lastName":"Smith","email":"alice@test.com"}')
check_status 201 "$status" "POST /api/customers (create Alice)"

# POST /api/customers (create second)
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/api/customers" \
    -H "Content-Type: application/json" \
    -d '{"firstName":"Bob","lastName":"Jones","email":"bob@test.com"}')
check_status 201 "$status" "POST /api/customers (create Bob)"

# GET /api/customers (list with data)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/customers")
check_status 200 "$status" "GET /api/customers (populated)"

# GET /api/customers/1
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/customers/1")
check_status 200 "$status" "GET /api/customers/1"

# GET /api/customers/999 (not found)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/customers/999")
check_status 404 "$status" "GET /api/customers/999 (not found)"

# PUT /api/customers/1
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X PUT "$BASE_URL/api/customers/1" \
    -H "Content-Type: application/json" \
    -d '{"firstName":"Alice","lastName":"Johnson","email":"alice@test.com"}')
check_status 200 "$status" "PUT /api/customers/1 (update)"

# DELETE /api/customers/2
status=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/customers/2")
check_status 204 "$status" "DELETE /api/customers/2"

echo ""

# --- 2. Account endpoints ---------------------------------------------------

info "=== Account Endpoints ==="

# GET /api/accounts (empty)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/accounts")
check_status 200 "$status" "GET /api/accounts (empty)"

# POST /api/accounts (create)
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/api/accounts" \
    -H "Content-Type: application/json" \
    -d '{"accountNumber":"ACC-001","balance":1000.00,"customerId":1}')
check_status 201 "$status" "POST /api/accounts (create ACC-001)"

# GET /api/accounts (populated)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/accounts")
check_status 200 "$status" "GET /api/accounts (populated)"

# GET /api/accounts/1
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/accounts/1")
check_status 200 "$status" "GET /api/accounts/1"

echo ""

# --- 3. Transaction endpoints -----------------------------------------------

info "=== Transaction Endpoints ==="

# GET /api/transactions (empty)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/transactions")
check_status 200 "$status" "GET /api/transactions (empty)"

# POST /api/transactions (create)
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/api/transactions" \
    -H "Content-Type: application/json" \
    -d '{"accountId":"1","amount":250.00,"type":"DEPOSIT"}')
check_status 201 "$status" "POST /api/transactions (create deposit)"

# GET /api/transactions (populated)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/transactions")
check_status 200 "$status" "GET /api/transactions (populated)"

echo ""

# --- 4. Compliance endpoints ------------------------------------------------

info "=== Compliance Endpoints ==="

# GET /api/compliance (empty)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/compliance")
check_status 200 "$status" "GET /api/compliance (empty)"

# POST /api/compliance (create)
status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/api/compliance" \
    -H "Content-Type: application/json" \
    -d '{"customerId":1,"checkType":"KYC","passed":true}')
check_status 201 "$status" "POST /api/compliance (create KYC check)"

# GET /api/compliance (populated)
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/compliance")
check_status 200 "$status" "GET /api/compliance (populated)"

echo ""

# --- 5. Index page ----------------------------------------------------------

info "=== Index Page ==="

status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/")
check_status 200 "$status" "GET / (index page)"

echo ""

# --- summary ----------------------------------------------------------------

echo "=========================================="
printf "  Results: %d passed, %d failed\n" "$PASS" "$FAIL"
echo "=========================================="

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
