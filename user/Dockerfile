FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/user.jar

ENTRYPOINT ["java", "-jar", "/app/user.jar"]