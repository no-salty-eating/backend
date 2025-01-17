FROM gradle:7.6-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 모든 소스 코드 복사
COPY . .

# Gradle Wrapper에 실행 권한 추가
RUN chmod +x gradlew

# Gradle 빌드 실행
RUN ./gradlew build

# 최종 이미지 생성
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar payment.jar

ENTRYPOINT ["java", "-jar", "/app/payment.jar"]
