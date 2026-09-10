FROM maven:3.9-eclipse-temurin-21 AS build
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
COPY config-server/src config-server/src
RUN mvn -B -pl config-server -am package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /workspace/config-server/target/config-server-*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
