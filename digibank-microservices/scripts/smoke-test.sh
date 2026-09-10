#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
DISCOVERY_URL="${DISCOVERY_URL:-http://localhost:8761}"
CONFIG_URL="${CONFIG_URL:-http://localhost:8888}"
CUSTOMER_URL="${CUSTOMER_URL:-http://localhost:8081}"
ACCOUNT_URL="${ACCOUNT_URL:-http://localhost:8082}"
TRANSACTION_URL="${TRANSACTION_URL:-http://localhost:8083}"
COMPLIANCE_URL="${COMPLIANCE_URL:-http://localhost:8084}"
NOTIFICATION_URL="${NOTIFICATION_URL:-http://localhost:8085}"
GATEWAY_USERNAME="${GATEWAY_USERNAME:-user}"
GATEWAY_PASSWORD="${GATEWAY_PASSWORD:-changeit-user}"
GATEWAY_ADMIN_USERNAME="${GATEWAY_ADMIN_USERNAME:-admin}"
GATEWAY_ADMIN_PASSWORD="${GATEWAY_ADMIN_PASSWORD:-changeit-admin}"

check() {
  local name="$1"
  local url="$2"
  local auth_user="${3:-}"
  local auth_password="${4:-}"
  printf 'Checking %-16s %s\n' "$name" "$url"
  local attempt
  for attempt in $(seq 1 30); do
    local response
    local -a curl_args=(--fail --silent --show-error --max-time 5
      -H 'Accept: application/json')
    if [[ -n "$auth_user" ]]; then
      curl_args+=(--user "${auth_user}:${auth_password}")
    fi
    if response="$(curl "${curl_args[@]}" "$url")" &&
      ! grep -qi '<!doctype html\|<html' <<<"$response"; then
      return 0
    fi
    sleep 2
  done
  echo "Smoke check failed after 60 seconds: $url" >&2
  return 1
}

check gateway-health "${BASE_URL}/actuator/health"
check customer-api "${BASE_URL}/api/customers" "$GATEWAY_USERNAME" "$GATEWAY_PASSWORD"
check account-api "${BASE_URL}/api/accounts" "$GATEWAY_USERNAME" "$GATEWAY_PASSWORD"
check transaction-api "${BASE_URL}/api/transactions" "$GATEWAY_USERNAME" "$GATEWAY_PASSWORD"
check compliance-api "${BASE_URL}/api/compliance" "$GATEWAY_ADMIN_USERNAME" "$GATEWAY_ADMIN_PASSWORD"
check customer-service "${CUSTOMER_URL}/api/customers"
check account-service "${ACCOUNT_URL}/api/accounts"
check transaction-service "${TRANSACTION_URL}/api/transactions"
check compliance-service "${COMPLIANCE_URL}/api/compliance"
check notification-service "${NOTIFICATION_URL}/actuator/health"
check discovery-registry "${DISCOVERY_URL}/registry"
check config-server "${CONFIG_URL}/config/customer-service"

echo "Microservice smoke checks passed."
