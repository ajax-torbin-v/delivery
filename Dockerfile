# Use a base image with JDK and Gradle
FROM amazoncorretto:22-alpine3.17-jdk

# Set the working directory for the runtime environment
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY ./build/libs/delivery-0.0.1-SNAPSHOT.jar app.jar

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
