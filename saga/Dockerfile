FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/saga.jar

ENTRYPOINT ["java", "-jar", "/app/saga.jar"]