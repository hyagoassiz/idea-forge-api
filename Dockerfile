FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy only what is needed to build (speeds up layer caching)
COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the fat jar from the build stage (use wildcard to be resilient to name changes)
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
