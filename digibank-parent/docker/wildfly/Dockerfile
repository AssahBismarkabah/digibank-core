# =============================================================================
# Digi Bank -- WildFly Deployment Image
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
FROM quay.io/wildfly/wildfly:40.0.1.Final-jdk17

ARG POSTGRESQL_VERSION=42.7.13

# --------------------------------------------------------------------------
# Install PostgreSQL JDBC driver as a WildFly module
# (Manual module.xml approach avoids CLI parsing issues in specific versions)
# --------------------------------------------------------------------------
ADD --chown=jboss:jboss \
    https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar \
    /tmp/postgresql.jar

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main && \
    cp /tmp/postgresql.jar $JBOSS_HOME/modules/org/postgresql/main/postgresql.jar && \
    cat > $JBOSS_HOME/modules/org/postgresql/main/module.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgresql">
    <resources>
        <resource-root path="postgresql.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
EOF

# --------------------------------------------------------------------------
# Configure standalone.xml with PostgreSQL driver and datasource
# (Direct XML patching is more reliable than CLI for Docker builds)
# --------------------------------------------------------------------------
RUN sed -i '/<\/datasources>/i\
    <datasource jndi-name="java:\/jboss\/datasources\/DigiBankDS" pool-name="DigiBankDS" enabled="true" use-java-context="true">\
        <connection-url>jdbc:postgresql:\/\/postgres:5432\/digibank_db<\/connection-url>\
        <driver>postgresql<\/driver>\
        <security user-name="digibank_user" password="digibank_pwd"\/>\
        <validation>\
            <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"\/>\
        <\/validation>\
    <\/datasource>' $JBOSS_HOME/standalone/configuration/standalone.xml && \
    sed -i '/<\/drivers>/i\
    <driver name="postgresql" module="org.postgresql">\
        <driver-class>org.postgresql.Driver<\/driver-class>\
    <\/driver>' $JBOSS_HOME/standalone/configuration/standalone.xml && \
    rm -f /tmp/postgresql.jar

# --------------------------------------------------------------------------
# Deploy the application WAR (auto-deployment directory)
# --------------------------------------------------------------------------
COPY --from=builder /build/digibank-app/target/digibank-app.war \
    $JBOSS_HOME/standalone/deployments/

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
