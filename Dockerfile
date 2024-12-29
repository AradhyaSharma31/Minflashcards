# Step 1: Use an OpenJDK base image
FROM openjdk:17-jdk-slim as build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy the local jar file to the container
COPY . .
#COPY target/FlashcardBackend-0.0.1-SNAPSHOT.jar /app/FlashcardBackend-0.0.1-SNAPSHOT.jar

# Step 4: Expose the port
EXPOSE 9030

# Step 5: Run the application
CMD ["java", "-jar", "/app/FlashcardBackend-0.0.1-SNAPSHOT.jar"]
