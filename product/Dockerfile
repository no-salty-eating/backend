FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/product.jar

ENTRYPOINT ["java", "-jar", "/app/product.jar"]