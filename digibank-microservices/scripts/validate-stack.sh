#!/usr/bin/env bash
set -euo pipefail

expected_services="account-db account-service api-gateway compliance-db compliance-service config-server customer-db customer-service discovery-server notification-service transaction-db transaction-service"
actual_services="$(docker compose -f docker-compose.yml ps --services --filter status=running | sort | tr '\n' ' ')"
expected_sorted="$(printf '%s\n' $expected_services | sort | tr '\n' ' ')"

if [[ "$actual_services" != "$expected_sorted" ]]; then
  echo "Running Compose services do not match the expected stack." >&2
  echo "Expected: $expected_sorted" >&2
  echo "Actual:   $actual_services" >&2
  docker compose -f docker-compose.yml ps >&2
  exit 1
fi

echo "All expected microservice containers are running."
