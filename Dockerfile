# 빌드용 -> JDK가 포함된 빌드 이미지는 용량이 크다.
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 실행용 -> Gradle, 소스코드, 캐시 등을 다 제거한 상태
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]