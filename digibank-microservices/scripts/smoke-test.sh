#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
DISCOVERY_URL="${DISCOVERY_URL:-http://localhost:8761}"
CONFIG_URL="${CONFIG_URL:-http://localhost:8888}"

check() {
  local name="$1"
  local url="$2"
  printf 'Checking %-16s %s\n' "$name" "$url"
  local attempt
  for attempt in $(seq 1 30); do
    local response
    if response="$(curl --fail --silent --show-error --max-time 5 \
      -H 'Accept: application/json' "$url")" &&
      ! grep -qi '<!doctype html\|<html' <<<"$response"; then
      return 0
    fi
    sleep 2
  done
  echo "Smoke check failed after 60 seconds: $url" >&2
  return 1
}

check gateway-health "${BASE_URL}/actuator/health"
check customer-api "${BASE_URL}/api/customers"
check account-api "${BASE_URL}/api/accounts"
check transaction-api "${BASE_URL}/api/transactions"
check compliance-api "${BASE_URL}/api/compliance"
check discovery-registry "${DISCOVERY_URL}/registry"
check config-server "${CONFIG_URL}/config/customer-service"

echo "Microservice smoke checks passed."
