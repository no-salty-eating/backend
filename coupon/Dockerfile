FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*.jar /app/coupon.jar

ENTRYPOINT ["java", "-jar", "/app/coupon.jar"]