FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/server.jar

ENTRYPOINT ["java", "-jar", "/app/server.jar"]