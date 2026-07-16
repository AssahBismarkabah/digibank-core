#!/usr/bin/env bash
# =============================================================================
# Digi Bank -- Smoke Test Suite
# Automated verification of all REST API endpoints.
# Usage: ./scripts/smoke-test.sh
# =============================================================================

set -euo pipefail

BASE_URL="${DIGIBANK_URL:-http://localhost:8080/digibank-app/api}"
PASS=0
FAIL=0
TOTAL=0

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_banner() {
    echo ""
    echo "============================================"
    echo "  Digi Bank -- Smoke Test Suite"
    echo "  $(date '+%Y-%m-%d %H:%M:%S')"
    echo "  Target: ${BASE_URL}"
    echo "============================================"
    echo ""
}

print_result() {
    local status="$1"
    local description="$2"
    TOTAL=$((TOTAL + 1))
    if [ "$status" = "PASS" ]; then
        echo -e "  ${GREEN}[PASS]${NC} ${description}"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}[FAIL]${NC} ${description}"
        FAIL=$((FAIL + 1))
    fi
}

print_section() {
    echo ""
    echo -e "${CYAN}--- $1 ---${NC}"
}

# ---------------------------------------------------------------------------
# Wait for WildFly to be ready (up to 120 seconds)
# ---------------------------------------------------------------------------
wait_for_server() {
    echo "Waiting for WildFly to be ready..."
    local max_attempts=60
    for i in $(seq 1 "$max_attempts"); do
        if curl -sf "${BASE_URL}/customers" > /dev/null 2>&1; then
            echo "WildFly is ready (attempt $i)"
            return 0
        fi
        sleep 2
    done
    echo -e "${RED}ERROR: WildFly did not start within $((max_attempts * 2)) seconds${NC}"
    exit 1
}

# ---------------------------------------------------------------------------
# Test an endpoint by HTTP status code
# ---------------------------------------------------------------------------
test_status() {
    local method="$1"
    local path="$2"
    local data="$3"
    local expected="$4"
    local description="$5"

    local url="${BASE_URL}${path}"
    local http_code

    if [ "$method" = "GET" ]; then
        http_code=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    elif [ "$method" = "DELETE" ]; then
        http_code=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$url")
    else
        http_code=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" \
            -H "Content-Type: application/json" -d "$data" "$url")
    fi

    if [ "$http_code" = "$expected" ]; then
        print_result "PASS" "${description} (HTTP ${http_code})"
    else
        print_result "FAIL" "${description} (expected ${expected}, got ${http_code})"
    fi
}

# ---------------------------------------------------------------------------
# Test that a POST response body contains expected fields
# ---------------------------------------------------------------------------
test_json_fields() {
    local path="$1"
    local data="$2"
    local expected_fields="$3"
    local description="$4"

    local url="${BASE_URL}${path}"
    local response
    response=$(curl -s -X POST -H "Content-Type: application/json" -d "$data" "$url")

    local all_found=true
    for field in $expected_fields; do
        if ! echo "$response" | grep -q "\"${field}\""; then
            all_found=false
            break
        fi
    done

    if [ "$all_found" = true ]; then
        print_result "PASS" "${description} (fields: ${expected_fields})"
    else
        local truncated
        truncated=$(echo "$response" | head -c 200)
        print_result "FAIL" "${description} -- response: ${truncated}"
    fi
}

# ---------------------------------------------------------------------------
# Test validation error response format
# ---------------------------------------------------------------------------
test_validation() {
    local path="$1"
    local data="$2"
    local description="$3"

    local url="${BASE_URL}${path}"
    local response
    response=$(curl -s -X POST -H "Content-Type: application/json" -d "$data" "$url")

    if echo "$response" | grep -q '"error"' && echo "$response" | grep -q '"violations"'; then
        print_result "PASS" "${description}"
    else
        local truncated
        truncated=$(echo "$response" | head -c 200)
        print_result "FAIL" "${description} -- response: ${truncated}"
    fi
}

# ===========================================================================
# MAIN
# ===========================================================================
print_banner
wait_for_server

# -----------------------------------------------------------------------
# Customers
# -----------------------------------------------------------------------
print_section "Customer API"

UNIQUE_SUFFIX=$(date +%s)

test_status "POST" "/customers" \
    "{\"firstName\":\"Alice\",\"lastName\":\"Smith\",\"email\":\"alice.${UNIQUE_SUFFIX}@example.com\"}" \
    201 "Create customer"

test_json_fields "/customers" \
    "{\"firstName\":\"Bob\",\"lastName\":\"Jones\",\"email\":\"bob.${UNIQUE_SUFFIX}@example.com\"}" \
    "id firstName lastName email" \
    "Create customer returns all fields"

test_status "GET" "/customers" "" 200 "List all customers"

test_status "GET" "/customers/1" "" 200 "Get customer by ID"

test_status "GET" "/customers/999" "" 404 "Get non-existent customer returns 404"

# -----------------------------------------------------------------------
# Accounts
# -----------------------------------------------------------------------
print_section "Account API"

test_status "POST" "/accounts" \
    "{\"customerId\":1,\"accountNumber\":\"DE1${UNIQUE_SUFFIX}\",\"accountType\":\"CHECKING\",\"currency\":\"EUR\",\"balance\":1000.00}" \
    201 "Create account"

test_json_fields "/accounts" \
    "{\"customerId\":1,\"accountNumber\":\"DE2${UNIQUE_SUFFIX}\",\"accountType\":\"SAVINGS\",\"currency\":\"EUR\",\"balance\":5000.00}" \
    "id accountNumber balance currency accountType" \
    "Create account returns all fields"

test_status "GET" "/accounts" "" 200 "List all accounts"

test_status "GET" "/accounts/1" "" 200 "Get account by ID"

test_status "GET" "/accounts/by-customer/1" "" 200 "Get accounts by customer ID"

# -----------------------------------------------------------------------
# Transactions
# -----------------------------------------------------------------------
print_section "Transaction API"

test_status "POST" "/transactions" \
    '{"accountId":1,"amount":250.00,"transactionType":"DEPOSIT","description":"Salary deposit"}' \
    201 "Create transaction"

test_json_fields "/transactions" \
    '{"accountId":1,"amount":50.00,"transactionType":"WITHDRAWAL","description":"ATM withdrawal"}' \
    "id referenceNumber amount transactionType transactionDate" \
    "Create transaction returns all fields"

test_status "GET" "/transactions" "" 200 "List all transactions"

test_status "GET" "/transactions/1" "" 200 "Get transaction by ID"

test_status "GET" "/transactions/by-account/1" "" 200 "Get transactions by account ID"

# -----------------------------------------------------------------------
# Compliance / KYC
# -----------------------------------------------------------------------
print_section "Compliance API"

test_status "POST" "/compliance" \
    '{"customerId":1,"checkType":"KYC","status":"PENDING","checkedBy":"SYSTEM","remarks":"Initial check"}' \
    201 "Create compliance check"

test_json_fields "/compliance" \
    '{"customerId":1,"checkType":"SANCTIONS_SCREENING","status":"PENDING","checkedBy":"SYSTEM","remarks":"Sanctions screening"}' \
    "id customerId checkType status checkedBy checkDate" \
    "Create compliance check returns all fields"

test_status "GET" "/compliance" "" 200 "List all compliance checks"

test_status "GET" "/compliance/1" "" 200 "Get compliance check by ID"

test_status "GET" "/compliance/by-customer/1" "" 200 "Get compliance checks by customer ID"

# -----------------------------------------------------------------------
# Validation
# -----------------------------------------------------------------------
print_section "Validation Error Handling"

test_validation "/customers" \
    '{"firstName":"","lastName":"","email":"bad-email"}' \
    "Rejects invalid customer input with error+violations JSON"

test_validation "/accounts" \
    '{"customerId":null,"balance":-50}' \
    "Rejects invalid account input with error+violations JSON"

test_validation "/transactions" \
    '{"amount":-10,"transactionType":""}' \
    "Rejects invalid transaction input with error+violations JSON"

# -----------------------------------------------------------------------
# Summary
# -----------------------------------------------------------------------
echo ""
echo "============================================"
if [ "$FAIL" -eq 0 ]; then
    echo -e "  ${GREEN}ALL ${TOTAL} TESTS PASSED${NC}"
else
    echo -e "  ${RED}${PASS} passed, ${FAIL} failed (${TOTAL} total)${NC}"
fi
echo "============================================"
echo ""

exit "$FAIL"
