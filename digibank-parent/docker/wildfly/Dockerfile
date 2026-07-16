# =============================================================================
# Digi Bank -- WildFly EE 10 Deployment Image
# Multi-stage build: compiles the Maven project, then creates a WildFly image
# with the PostgreSQL driver, datasource, and application pre-configured.
# =============================================================================

# Stage 1: Build the WAR
FROM maven:3-eclipse-temurin-17 AS builder
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

# Stage 2: Create runnable WildFly image
# Uses WildFly 40 (Jakarta EE 11, backward compatible with EE 10 apps)
FROM quay.io/wildfly/wildfly:40.0.1.Final-jdk17

ARG POSTGRESQL_VERSION=42.7.13

# Download PostgreSQL JDBC driver
ADD --chown=jboss:jboss \
    https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar \
    /tmp/postgresql.jar

# Copy CLI configuration script
COPY --chown=jboss:jboss docker/wildfly/configure.cli /tmp/configure.cli

# Deploy the application WAR (auto-deployment directory)
COPY --from=builder /build/digibank-app/target/digibank-app.war \
    /opt/jboss/wildfly/standalone/deployments/

# Execute CLI to configure PostgreSQL driver and datasource
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=/tmp/configure.cli && \
    rm -f /tmp/configure.cli /tmp/postgresql.jar

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
