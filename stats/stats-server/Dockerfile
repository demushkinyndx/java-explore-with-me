FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml lombok.config checkstyle.xml suppressions.xml ./
COPY stats ./stats
COPY ewm-service ./ewm-service
RUN mvn -pl stats/stats-server -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/stats/stats-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
