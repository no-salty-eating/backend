FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/gateway.jar

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]