# GitHub Actions CI

This repository uses GitHub Actions to validate the main project signals before merge.

## What is validated

- Build and test execution across the full multi-module Maven project.
- The Spring Boot application context smoke test in the app module.
- End-to-end smoke validation against both supported deployment modes:
  - embedded Tomcat via Docker Compose
  - WildFly via Docker Compose

The workflow reuses the repository's existing entry points instead of introducing separate validation logic:

- Maven test/build commands from the parent module
- The existing smoke script at scripts/smoke-test.sh
- Docker Compose profiles defined in docker-compose.yml

Each smoke job also cleans up compose containers and volumes so later runs start from a fresh environment.
