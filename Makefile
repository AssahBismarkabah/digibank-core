.PHONY: build build-skip-tests up up-wildfly down logs test smoke-test verify clean help

PROJECT_DIR := digibank-parent

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project (compile, test, package)
	cd $(PROJECT_DIR) && mvn clean install

build-skip-tests: ## Build the project skipping tests
	cd $(PROJECT_DIR) && mvn clean install -DskipTests

up: ## Start PostgreSQL + Spring Boot (embedded Tomcat, dev mode)
	cd $(PROJECT_DIR) && docker compose --profile dev up --build -d

up-wildfly: ## Start PostgreSQL + WildFly (production mode)
	cd $(PROJECT_DIR) && docker compose --profile wildfly up --build -d

up-foreground: ## Start dev mode in foreground (see logs)
	cd $(PROJECT_DIR) && docker compose --profile dev up --build

down: ## Stop all containers
	cd $(PROJECT_DIR) && docker compose down

down-volumes: ## Stop all containers and remove volumes (wipes DB)
	cd $(PROJECT_DIR) && docker compose down -v

logs: ## Tail logs from all containers
	cd $(PROJECT_DIR) && docker compose logs -f

logs-app: ## Tail logs from the app container only
	cd $(PROJECT_DIR) && docker compose logs -f app

logs-db: ## Tail logs from the database container only
	cd $(PROJECT_DIR) && docker compose logs -f postgres

test: ## Run all tests
	cd $(PROJECT_DIR) && mvn test

smoke-test: ## Run smoke tests against a running instance
	$(PROJECT_DIR)/scripts/smoke-test.sh

verify: build up smoke-test down ## Full verification: build, start, smoke-test, stop

clean: ## Clean all build artifacts
	cd $(PROJECT_DIR) && mvn clean
