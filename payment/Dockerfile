FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/payment.jar

ENTRYPOINT ["java", "-jar", "/app/payment.jar"]