# Dockerfile
FROM openjdk:17


# JAR 파일이 빌드 결과로 이 위치에 있다고 가정 (빌드 스크립트에 맞게 수정)
COPY build/libs/*.jar app.jar

# 환경변수로 포트(옵션)
ENV SERVER_PORT=9004

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
