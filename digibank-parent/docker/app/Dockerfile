# =============================================================================
# Digi Bank -- Spring Boot Embedded Tomcat Deployment
# Multi-stage build: compiles the Maven project, creates a slim JRE image
# with the Spring Boot fat JAR.
# =============================================================================

# Stage 1: Build the fat JAR
FROM maven:3-eclipse-temurin-25 AS builder
WORKDIR /build

COPY pom.xml .
COPY digibank-shared/pom.xml digibank-shared/
COPY digibank-customer/pom.xml digibank-customer/
COPY digibank-account/pom.xml digibank-account/
COPY digibank-transaction/pom.xml digibank-transaction/
COPY digibank-compliance/pom.xml digibank-compliance/
COPY digibank-index/pom.xml digibank-index/
COPY digibank-app/pom.xml digibank-app/

RUN mvn dependency:go-offline -pl digibank-app -am -B 2>/dev/null || true

COPY digibank-shared/src digibank-shared/src
COPY digibank-customer/src digibank-customer/src
COPY digibank-account/src digibank-account/src
COPY digibank-transaction/src digibank-transaction/src
COPY digibank-compliance/src digibank-compliance/src
COPY digibank-index/src digibank-index/src
COPY digibank-app/src digibank-app/src

RUN mvn -pl digibank-app -am clean package -DskipTests -B

# Stage 2: Slim JRE runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /build/digibank-app/target/digibank-app.war app.war
EXPOSE 8080
CMD ["java", "-jar", "app.war"]
