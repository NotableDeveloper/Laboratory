FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# 필요한 도구 설치
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# 빌드된 JAR 파일 복사 (프로젝트 루트에서 실행 시)
COPY build/libs/Laboratory.jar ./app.jar

# 로그백 설정 파일 복사 (있는 경우)
COPY src/main/resources ./config

# TCP 서버 포트 노출
EXPOSE 9000

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD echo "PING" | nc -w 1 localhost 9000 || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["9000"]
