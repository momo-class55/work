# ---------- 1단계: 빌드 ----------
# 공식 이미지에 JDK 21 + Maven이 포함되어 있음 (별도 Java 설치 불필요)
FROM maven:3.9.6-eclipse-temurin-21 AS build

# 애플리케이션 작업 디렉터리
WORKDIR /app

# JDK가 이미지에 포함되어 있는지 빌드 로그로 확인
RUN java -version

# Maven 의존성만 먼저 복사 → 레이어 캐시 활용
COPY pom.xml .

# 의존성 미리 받아 두기 (오프라인에 가깝게)
RUN mvn dependency:go-offline

# 소스 복사 후 패키징 (테스트는 CI에서 수행한다고 가정)
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- 2단계: 실행 ----------
# JRE만 포함 (JDK보다 이미지가 작음)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# 빌드 단계에서 만든 JAR만 복사
COPY --from=build /app/target/*.jar app.jar

# 서비스 포트
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
