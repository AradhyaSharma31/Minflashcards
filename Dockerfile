# Step 1: Build Stage
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy only the necessary files for Maven to build the application
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Step 2: Runtime Stage
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar /app/FlashcardBackend-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on
EXPOSE 9030

# Run the application
CMD ["java", "-jar", "/app/FlashcardBackend-0.0.1-SNAPSHOT.jar"]
