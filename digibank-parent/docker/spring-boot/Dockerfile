# =============================================================================
# Digi Bank -- Spring Boot WAR Deployment on WildFly
# Multi-stage build: compiles the Maven project, then creates a WildFly image
# with the Spring Boot WAR pre-deployed.
# =============================================================================

# Stage 1: Build the WAR
FROM maven:3-eclipse-temurin-25 AS builder
WORKDIR /build

# 1a. Copy only POMs first to leverage layer caching for dependencies
COPY pom.xml .
COPY digibank-shared/pom.xml digibank-shared/
COPY digibank-customer/pom.xml digibank-customer/
COPY digibank-account/pom.xml digibank-account/
COPY digibank-transaction/pom.xml digibank-transaction/
COPY digibank-compliance/pom.xml digibank-compliance/
COPY digibank-index/pom.xml digibank-index/
COPY digibank-app/pom.xml digibank-app/

RUN mvn dependency:go-offline -pl digibank-app -am -B 2>/dev/null || true

# 1b. Copy source files and build
COPY digibank-shared/src digibank-shared/src
COPY digibank-customer/src digibank-customer/src
COPY digibank-account/src digibank-account/src
COPY digibank-transaction/src digibank-transaction/src
COPY digibank-compliance/src digibank-compliance/src
COPY digibank-index/src digibank-index/src
COPY digibank-app/src digibank-app/src

RUN mvn -pl digibank-app -am clean package -DskipTests -B

# Stage 2: Create WildFly runtime image
FROM quay.io/wildfly/wildfly:40.0.1.Final-jdk25

# Copy the built WAR to WildFly's auto-deployment directory
COPY --from=builder /build/digibank-app/target/digibank-app.war \
    /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
