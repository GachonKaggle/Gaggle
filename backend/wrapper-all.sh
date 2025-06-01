#!/bin/bash
set -eu  # 에러 발생 시 즉시 중단, 미정의 변수 사용 시 중단

# 서버 디렉터리 목록
SERVERS="api-gateway eureka-server grading-server leaderboard-server user-server"

for dir in $SERVERS; do
    echo "========================="
    echo "$dir 에서 Gradle Wrapper(7.5.1) 생성/갱신 중..."

    cd "$dir"

    # gradle wrapper 명령 실행 (gradlew가 있든 없든 항상 7.5.1로 갱신)
    gradle wrapper --gradle-version 7.5.1

    cd ..
done

echo "========================="
echo "모든 서버의 Gradle Wrapper(7.5.1) 생성/갱신이 완료되었습니다."
