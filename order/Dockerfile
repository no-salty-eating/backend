FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/order.jar

ENTRYPOINT ["java", "-jar", "/app/order.jar"]