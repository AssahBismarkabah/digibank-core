COMPOSE := docker compose -f docker-compose.yml
MVN := mvn -B -f pom.xml

.PHONY: help build test package image-build up down restart status logs smoke validate-stack newman-scan zap-scan clean

help: ## Show available microservice commands
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "%-16s %s\n", $$1, $$2}'

build: ## Compile and run all microservice tests
	$(MVN) clean verify

test: ## Run all microservice tests
	$(MVN) test

package: ## Package all microservices without rerunning tests
	$(MVN) package -DskipTests

image-build: ## Build all service and infrastructure images
	$(COMPOSE) build

up: ## Start the complete microservice stack in the background
	$(COMPOSE) up -d

down: ## Stop the microservice stack
	$(COMPOSE) down

restart: ## Recreate the stack from the current images
	$(COMPOSE) up -d --force-recreate

status: ## Show container and database health status
	$(COMPOSE) ps

logs: ## Follow logs from all microservices
	$(COMPOSE) logs -f

smoke: ## Verify gateway, registry, config, and service endpoints
	./scripts/smoke-test.sh

validate-stack: ## Verify every Compose service is running
	./scripts/validate-stack.sh

newman-scan: ## Replay the API security collection against the local stack
	../dast/postman/newman-run.sh --env local --informational

zap-scan: ## Run an OWASP ZAP baseline scan against the local gateway
	@command -v docker >/dev/null 2>&1 || { echo "Docker is required for the ZAP scan" >&2; exit 1; }
	docker run --rm --network host -v "$(CURDIR)/../dast/reports:/zap/wrk:rw" zaproxy/zap-stable zap-baseline.py \
		-t http://127.0.0.1:8080/ -r zap-report.html -J zap-report.json -w zap-report.md || test $$? -eq 2

clean: ## Stop the stack and remove its database volumes
	$(COMPOSE) down -v
