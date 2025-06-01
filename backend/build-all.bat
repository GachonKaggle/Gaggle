@echo off
rem =================================================
rem  각 서버 디렉터리로 이동하여 gradlew clean build 실행
rem =================================================

set SERVERS=api-gateway eureka-server grading-server leaderboard-server user-server

for %%D in (%SERVERS%) do (
    echo =========================
    echo %%D 빌드 중...
    pushd %%D
    call gradlew.bat clean build
    popd
)

echo =========================
echo 모든 서버 빌드가 종료되었습니다.
pause
