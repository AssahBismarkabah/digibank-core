# =============================================================================
# Digi Bank -- Spring Boot 4.x Modular Monolith
# Makefile for building, deploying, and testing via Docker Compose
# =============================================================================

.PHONY: build build-skip-tests up up-wildfly up-foreground down down-volumes logs logs-app logs-db test smoke-test verify clean help

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project (compile, test, package)
	mvn clean install

build-skip-tests: ## Build the project skipping tests
	mvn clean install -DskipTests

up: ## Start PostgreSQL + Spring Boot (embedded Tomcat, dev mode)
	docker compose --profile dev up --build -d

up-wildfly: ## Start PostgreSQL + WildFly (production mode)
	docker compose --profile wildfly up --build -d

up-foreground: ## Start dev mode in foreground (see logs)
	docker compose --profile dev up --build

down: ## Stop all containers
	docker compose down

down-volumes: ## Stop all containers and remove volumes (wipes DB)
	docker compose down -v

logs: ## Tail logs from all containers
	docker compose logs -f

logs-app: ## Tail logs from the app container only
	docker compose logs -f app

logs-db: ## Tail logs from the database container only
	docker compose logs -f postgres

test: ## Run all tests
	mvn test

smoke-test: ## Run smoke tests against a running instance
	scripts/smoke-test.sh

verify: build up smoke-test down ## Full verification: build, start, smoke-test, stop

clean: ## Clean all build artifacts
	mvn clean
