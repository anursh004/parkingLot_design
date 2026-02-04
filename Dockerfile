#
# Multi-stage build:
# 1) Build the Spring Boot jar with Maven + JDK 17
# 2) Run it on a lightweight JRE 17 image
#

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache deps first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package


FROM eclipse-temurin:17-jre
WORKDIR /app

# Spring Boot defaults to 8080; matches application.yml
EXPOSE 8080

# Copy the built jar (name varies; take the only jar in target)
COPY --from=build /app/target/*.jar /app/app.jar

# Good default JVM flags for containers
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]

