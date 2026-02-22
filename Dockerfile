# Use a base image that includes JDK
FROM openjdk:17-jdk-slim

# Create a directory for the application
WORKDIR /app

# Copy the built JAR file into the container
COPY target/fxrate-0.0.1-SNAPSHOT.jar fxrate.jar

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "fxrate.jar"]
