# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build with limited memory to avoid OOM on free tier
RUN mvn clean package -DskipTests -Dmaven.compiler.fork=false -T 1

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Memory-optimized JVM flags for free tier (512MB RAM)
ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-XX:+UseSerialGC", "-jar", "app.jar"]
