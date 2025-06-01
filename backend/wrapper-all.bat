@echo off
rem =================================================
rem  각 서버 디렉터리로 이동하여 Gradle Wrapper 생성/갱신 (Gradle 7.5.1)
rem =================================================

set SERVERS=api-gateway eureka-server grading-server leaderboard-server user-server

for %%D in (%SERVERS%) do (
    echo =========================
    echo %%D 에서 Gradle Wrapper 생성 중...
    pushd %%D

    gradle wrapper --gradle-version 7.5.1

    popd
)

echo =========================
echo 모든 서버의 Gradle Wrapper 생성/갱신이 완료되었습니다.
pause
