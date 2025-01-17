# Gradle 빌드 단계
FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build


# 실행 단계
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar server.jar
ENTRYPOINT ["java", "-jar", "/app/server.jar"]
