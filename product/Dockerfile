# Base image
FROM openjdk:17-jdk-slim

# curl 설치
RUN apt-get update && apt-get install -y curl && apt-get clean

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY build/libs/*SNAPSHOT.jar product-service.

# Copy the Loki and Prometheus config files
COPY loki/loki-config.yml /app/loki-config.yml
COPY prometheus/prometheus.yml /app/prometheus.yml

# Expose the service port
EXPOSE 19096

# Run the application
ENTRYPOINT ["java", "-jar", "product-service.jar"]