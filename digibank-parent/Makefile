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
	docker compose build --no-cache wildfly
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
# Verify the deployment with curl
# ---------------------------------------------------------------------------
verify:
	@echo "==> Verifying Digi Bank REST endpoints..."
	@echo "--- Customers ---"
	curl -s http://localhost:8080/digibank-app/api/customers | head -c 200
	@echo ""
	@echo "--- Accounts ---"
	curl -s http://localhost:8080/digibank-app/api/accounts | head -c 200
	@echo ""
	@echo "--- Customer 1 ---"
	curl -s http://localhost:8080/digibank-app/api/customers/1 | head -c 200
	@echo ""
	@echo "==> Verify complete."

# ---------------------------------------------------------------------------
# Clean everything
# ---------------------------------------------------------------------------
clean:
	mvn clean
	docker compose down -v
	docker system prune -f
