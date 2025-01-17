FROM openjdk:17-alpine AS build

WORKDIR /app

# 소스 코드 복사
COPY . .

# Gradle을 사용하여 빌드
RUN ./gradlew build

# 실행 단계
FROM openjdk:17-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/user.jar

ENTRYPOINT ["java", "-jar", "/app/user.jar"]