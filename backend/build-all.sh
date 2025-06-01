#!/bin/bash
for dir in api-gateway eureka-server grading-server leaderboard-server user-server; do
    echo "========================="
    echo "$dir 빌드 중..."
    cd $dir
    ./gradlew clean build
    cd ..
done


# #!/bin/bash

# # 서비스 폴더 리스트
# services=(
#   api-gateway
#   eureka-server
#   result-server
#   comparision-server
#   leaderboard-server
# )

# # 추가할 Eureka 설정
# read -r -d '' EUREKA_CONFIG << EOL

# # === Eureka 설정 추가 ===
# spring.application.name=서비스이름_여기에_맞게_수정
# eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
# eureka.client.register-with-eureka=true
# eureka.client.fetch-registry=true
# EOL

# for service in "${services[@]}"
# do
#   prop_file="$service/src/main/resources/application.properties"
#   # 파일이 없으면 생성
#   touch "$prop_file"
#   # 서비스 이름 자동 반영
#   service_name=$(echo $service | tr '-' '_')
#   # 실제로 추가
#   echo "$EUREKA_CONFIG" | sed "s/서비스이름_여기에_맞게_수정/$service_name/" >> "$prop_file"
#   echo "$prop_file 에 Eureka 설정 추가 완료"
# done
