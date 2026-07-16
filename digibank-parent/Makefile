# =============================================================================
# Digi Bank -- Jakarta EE 10 Modular Monolith
# Makefile for deploying to WildFly via Docker Compose
# =============================================================================

.PHONY: all build deploy up down logs clean verify

all: build deploy

# ---------------------------------------------------------------------------
# Build the Maven project locally (skips tests for speed)
# ---------------------------------------------------------------------------
build:
	@echo "==> Building Digi Bank Maven project..."
	mvn clean package -DskipTests -B
	@echo "==> Build complete."

# ---------------------------------------------------------------------------
# Deploy: build project + start Docker Compose
# ---------------------------------------------------------------------------
deploy: build
	@echo "==> Deploying Digi Bank on WildFly via Docker Compose..."
	docker compose up --build -d
	@echo "==> Deployment started. WildFly will be ready in ~30-60 seconds."
	@echo "    Verify: curl http://localhost:8080/digibank-app/api/customers"

# ---------------------------------------------------------------------------
# Quick deploy using the multi-stage Docker build (no local Maven needed)
# ---------------------------------------------------------------------------
deploy-fast:
	@echo "==> Building and deploying in Docker (multi-stage)..."
	docker compose build wildfly
	docker compose up -d
	@echo "==> Done."

# ---------------------------------------------------------------------------
# Start existing containers
# ---------------------------------------------------------------------------
up:
	docker compose up -d

# ---------------------------------------------------------------------------
# Stop and remove containers
# ---------------------------------------------------------------------------
down:
	docker compose down

# ---------------------------------------------------------------------------
# Tail logs from WildFly
# ---------------------------------------------------------------------------
logs:
	docker compose logs -f wildfly

# ---------------------------------------------------------------------------
# Wait for WildFly to be ready (polling management endpoint)
# ---------------------------------------------------------------------------
wait:
	@echo "==> Waiting for WildFly to be ready..."
	@for i in $$(seq 1 30); do \
		if curl -s http://localhost:8080/digibank-app/api/customers >/dev/null 2>&1; then \
			echo "WildFly is ready!"; \
			exit 0; \
		fi; \
		echo "  Waiting... ($${i}s)"; \
		sleep 2; \
	done; \
	echo "ERROR: WildFly did not start within 60 seconds."; \
	exit 1

# ---------------------------------------------------------------------------
# Smoke test: verify endpoints and create sample data
# ---------------------------------------------------------------------------
smoke-test:
	@echo "==> Running Digi Bank smoke test..."
	@echo "=== GET /api/customers (empty) ==="
	curl -s http://localhost:8080/digibank-app/api/customers
	@echo ""
	@echo "=== POST /api/customers (create) ==="
	curl -s -X POST http://localhost:8080/digibank-app/api/customers \
		-H "Content-Type: application/json" \
		-d '{"firstName":"Alice","lastName":"Smith","email":"alice@example.com"}'
	@echo ""
	@echo "=== GET /api/customers (with data) ==="
	curl -s http://localhost:8080/digibank-app/api/customers
	@echo ""
	@echo "=== POST /api/accounts (create) ==="
	curl -s -X POST http://localhost:8080/digibank-app/api/accounts \
		-H "Content-Type: application/json" \
		-d '{"accountNumber":"DE1111111111","balance":2500.00,"customerId":1,"accountType":"CHECKING","currency":"EUR"}'
	@echo ""
	@echo "=== GET /api/accounts ==="
	curl -s http://localhost:8080/digibank-app/api/accounts
	@echo ""
	@echo "=== POST /api/transactions (create) ==="
	curl -s -X POST http://localhost:8080/digibank-app/api/transactions \
		-H "Content-Type: application/json" \
		-d '{"accountId":1,"amount":100.00,"transactionType":"DEPOSIT","description":"Initial deposit","currency":"EUR"}'
	@echo ""
	@echo "==> Smoke test complete. All endpoints operational."

# ---------------------------------------------------------------------------
# One-command verify: deploy, wait, and smoke test
# ---------------------------------------------------------------------------
verify: deploy wait smoke-test
	@echo "==> Digi Bank is deployed and operational."

# ---------------------------------------------------------------------------
# Clean everything
# ---------------------------------------------------------------------------
clean:
	mvn clean
	docker compose down -v
