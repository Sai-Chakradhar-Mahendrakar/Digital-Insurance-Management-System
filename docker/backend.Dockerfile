# Stage 1: Build
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/insurance/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/insurance/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java","-jar","app.jar"]
