FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml
COPY transaction-service/pom.xml transaction-service/pom.xml
COPY compliance-service/pom.xml compliance-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY account-service/src account-service/src
RUN mvn -B -pl account-service -am package -DskipTests
FROM eclipse-temurin:21-jre-alpine
RUN apk upgrade --no-cache
WORKDIR /app
RUN addgroup -S digibank && adduser -S -G digibank digibank
COPY --from=build --chown=digibank:digibank /workspace/account-service/target/account-service-*.jar app.jar
USER digibank
ENTRYPOINT ["java", "-jar", "app.jar"]
