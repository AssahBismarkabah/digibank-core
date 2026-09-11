COMPOSE := docker compose -f docker-compose.yml
MVN := mvn -B -f pom.xml
DEPENDENCY_CHECK_DATA_DIR ?= $(HOME)/.m2/repository/org/owasp/dependency-check-data
DEPENDENCY_CHECK_SUPPRESSIONS ?= $(CURDIR)/dependency-check-suppressions.xml

ifneq (,$(wildcard ../.env))
include ../.env
export SONAR_HOST_URL SONAR_ORGANIZATION SONAR_PROJECT_KEY SONAR_TOKEN
endif

SONAR_HOST_URL := $(or $(SONAR_HOST_URL),http://localhost:9000)
export SONAR_HOST_URL SONAR_TOKEN NVD_API_KEY

.PHONY: help build test package sast-dependency-check sast-pitest sast-sonar sast image-build container-scan up down restart status logs smoke validate-stack newman-scan zap-scan clean

help: ## Show available microservice commands
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "%-16s %s\n", $$1, $$2}'

build: ## Compile and run all microservice tests
	$(MVN) clean verify

test: ## Run all microservice tests
	$(MVN) test

package: ## Package all microservices without rerunning tests
	$(MVN) package -DskipTests

sast-dependency-check: ## Generate OWASP Dependency-Check reports
	@test -n "$$NVD_API_KEY" || (echo "NVD_API_KEY is required; set it in the ignored root .env or the shell" >&2; exit 1)
	$(MVN) org.owasp:dependency-check-maven:12.1.0:check -DnvdApiKey=$$NVD_API_KEY -Ddata.directory=$(DEPENDENCY_CHECK_DATA_DIR) -DsuppressionFiles=$(DEPENDENCY_CHECK_SUPPRESSIONS) -DnvdValidForHours=168 -Danalyzer.ossindex.enabled=false -Dformats=HTML,JSON -DfailBuildOnCVSS=11

sast-pitest: ## Generate PITest mutation-testing reports
	$(MVN) org.pitest:pitest-maven:1.17.4:mutationCoverage

sast-sonar: ## Send the project to a configured SonarQube server
	@test -n "$$SONAR_HOST_URL" || (echo "SONAR_HOST_URL is required; set it in the ignored root .env or the shell" >&2; exit 1)
	@test -n "$$SONAR_ORGANIZATION" || (echo "SONAR_ORGANIZATION is required for SonarCloud" >&2; exit 1)
	@test -n "$$SONAR_PROJECT_KEY" || (echo "SONAR_PROJECT_KEY is required for SonarCloud" >&2; exit 1)
	@test -n "$$SONAR_TOKEN" || (echo "SONAR_TOKEN is required; set it in the ignored root .env or the shell" >&2; exit 1)
	$(MVN) verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.8.0.7211:sonar -Dsonar.host.url=$(SONAR_HOST_URL) -Dsonar.organization=$(SONAR_ORGANIZATION) -Dsonar.projectKey=$(SONAR_PROJECT_KEY)

sast: ## Generate Dependency-Check and PITest reports
	$(MAKE) sast-dependency-check
	$(MAKE) sast-pitest

image-build: ## Build all service and infrastructure images
	$(COMPOSE) build

container-scan: ## Scan built images and container configuration with Trivy
	@command -v trivy >/dev/null 2>&1 || { echo "Trivy is required; install it or use the CI container-scan job" >&2; exit 1; }
	@mkdir -p ../dast/reports/container-scan
	@set -u; status=0; for image in $$($(COMPOSE) config --images | sort -u | grep -v '^postgres:'); do \
		echo "Scanning $$image"; \
		trivy image --scanners vuln,secret --format json --output "../dast/reports/container-scan/$${image##*/}.json" --ignore-unfixed --exit-code 0 "$$image"; \
		trivy image --scanners vuln,secret --format sarif --output "../dast/reports/container-scan/$${image##*/}.sarif" --ignore-unfixed --exit-code 0 "$$image"; \
		trivy image --scanners vuln,secret --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 "$$image" || status=1; \
	done; \
	trivy config --format json --output ../dast/reports/container-scan/docker-compose-config.json --exit-code 0 docker-compose.yml; \
	trivy config --format sarif --output ../dast/reports/container-scan/docker-compose-config.sarif --exit-code 0 docker-compose.yml; \
	trivy config --severity HIGH,CRITICAL --exit-code 1 docker-compose.yml || status=1; \
	trivy fs --scanners secret,misconfig --skip-dirs .git --skip-dirs target --skip-dirs dast/reports --skip-files "**/pom.xml" --format json --output ../dast/reports/container-scan/filesystem.json --exit-code 0 .; \
	trivy fs --scanners secret,misconfig --skip-dirs .git --skip-dirs target --skip-dirs dast/reports --skip-files "**/pom.xml" --format sarif --output ../dast/reports/container-scan/filesystem.sarif --exit-code 0 .; \
	trivy fs --scanners secret,misconfig --skip-dirs .git --skip-dirs target --skip-dirs dast/reports --skip-files "**/pom.xml" --severity HIGH,CRITICAL --exit-code 1 . || status=1; \
	exit $$status

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
	../dast/postman/newman-run.sh --env local

zap-scan: ## Run an OWASP ZAP baseline scan against the local gateway
	@command -v docker >/dev/null 2>&1 || { echo "Docker is required for the ZAP scan" >&2; exit 1; }
	docker run --rm --network host -v "$(CURDIR)/../dast/reports:/zap/wrk:rw" zaproxy/zap-stable zap-baseline.py \
		-t http://127.0.0.1:8080/ -r zap-report.html -J zap-report.json -w zap-report.md || test $$? -eq 2

clean: ## Stop the stack and remove its database volumes
	$(COMPOSE) down -v
