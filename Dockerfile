# Stage 1: Build the app with Gradle and JDK 17
FROM gradle:8.0-jdk17 AS build

WORKDIR /app

# Copy source code
COPY . .

# Build the fat jar without running tests for speed (optional)
RUN ./gradlew clean bootJar -x test

# Stage 2: Run the app on a slim OpenJDK 17 image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Optional: persist temp files if needed by your app
VOLUME /tmp

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
