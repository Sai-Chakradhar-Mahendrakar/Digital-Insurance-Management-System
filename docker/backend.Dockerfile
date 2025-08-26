FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml from the correct location
COPY backend/insurance/.mvn ./.mvn
COPY backend/insurance/mvnw .
COPY backend/insurance/mvnw.cmd .
COPY backend/insurance/pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (using Maven wrapper)
RUN ./mvnw dependency:go-offline -B

# Copy source code from the correct location
COPY backend/insurance/src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR file
COPY --from=0 /app/target/insurance-*.jar app.jar

# Expose port (adjust if your app uses a different port)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
