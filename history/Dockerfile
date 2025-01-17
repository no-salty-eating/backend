FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/history.jar

ENTRYPOINT ["java", "-jar", "/app/history.jar"]