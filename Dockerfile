# 런타임 스테이지 전용으로 경량화 (빌드 스테이지 제거)
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 ENV로 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 깃허브 액션즈가 바깥에서 빌드해둔 최신 jar 파일을 이미지 내부로 곧바로 복사
COPY build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./app.jar

# 80 포트 노출
EXPOSE 80

# jar 파일 실행
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]