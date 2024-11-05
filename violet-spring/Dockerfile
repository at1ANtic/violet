# Build stage
FROM maven:3.6.3-jdk-8-slim AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean test package

# Package stage
FROM openjdk:8-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-Xms512m","-Xmx512m","-Dfile.encoding=UTF-8","-jar","app.jar","--spring.profiles.active=prod"]