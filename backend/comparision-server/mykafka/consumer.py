import os
import time
import zipfile
import uuid
import shutil
import cv2
import math
from kafka import KafkaConsumer
from mykafka.producer import send_progress, send_result  # 앞서 만든 producer 함수 import
from utils import calculate_psnr, calculate_ssim

UPLOAD_FOLDER = './uploads'
TEMP_FOLDER = './temp'
REFERENCE_FOLDER = './static/reference'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(TEMP_FOLDER, exist_ok=True)

def process_zip_message(message):
    import base64
    import json

    print("\n=== [1] process_zip_message: 메시지 도착 ===")
    try:
        msg_text = message.value.decode()
        print(f"수신 원본(디코딩 후): {msg_text[:200]}...")
        msg = json.loads(msg_text)
    except Exception as e:
        print(f"[에러] JSON 디코드 실패: {e}")
        send_result(userId, requestId, loginId, None, None, task)
        return

    token = msg.get('token')
    userId = msg.get('userId')
    loginId = msg.get('loginId')
    requestId = msg.get('requestId') or str(uuid.uuid4())
    task = msg.get('task')
    base64zip = msg.get('zipFile')

    print(f"token: {token}")
    print(f"userId: {userId}")
    print(f"loginId: {loginId}")
    print(f"requestId: {requestId}")
    print(f"task: {task}")
    print(f"base64zip: 앞 100자: {str(base64zip)[:100]} ...")

    # zip 파일 저장
    try:
        zip_bytes = base64.b64decode(base64zip)
        zip_filename = f"{loginId}_{requestId}.zip"
        zip_path = os.path.join(UPLOAD_FOLDER, zip_filename)
        with open(zip_path, "wb") as f:
            f.write(zip_bytes)
        print(f"[2] zip 파일 저장 완료: {zip_path} ({len(zip_bytes)} bytes)")
    except Exception as e:
        print(f"[에러] zip 파일 저장 실패: {e}")
        send_result(token, userId, requestId, loginId, None, None, task)
        return

    extract_path = os.path.join(TEMP_FOLDER, os.path.splitext(zip_filename)[0])
    try:
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            zip_ref.extractall(extract_path)
        print(f"[3] 압축 해제 완료. 해제 경로: {extract_path}")
        print(f"[3-1] 압축 파일 개수: {os.listdir(extract_path)}")
    except Exception as e:
        print(f"[에러] 압축 해제 실패: {e}")
        send_result(token,userId, requestId, loginId, None, None, task)
        return

    extract_file = os.path.join(extract_path, "result")
    try:
        file_list = sorted([f for f in os.listdir(extract_file) if os.path.isfile(os.path.join(extract_file, f))])
    except Exception as e:
        print(f"[에러] 압축 내 'result' 폴더 없음 또는 파일 목록 조회 실패: {e}")
        # 임시 파일/폴더 정리
        if os.path.exists(extract_path):
            shutil.rmtree(extract_path)
        if os.path.exists(zip_path):
            os.remove(zip_path)
        print("=== [완료: result 폴더 없음으로 중단] ===\n")
        send_result(token, userId, requestId, loginId, None, None, task)
        return


    # === 여기서부터 task별 기준폴더 사용과 없는 경우 에러 처리 ===
    REFERENCE_FOLDER_BASE = './static/reference'
    reference_task_folder = os.path.join(REFERENCE_FOLDER_BASE, task)

    if not os.path.exists(reference_task_folder):
        error_msg = f"[에러] 기준 폴더 없음: {reference_task_folder}"
        print(error_msg)
        # 임시 파일/폴더 정리
        if os.path.exists(extract_path):
            shutil.rmtree(extract_path)
        if os.path.exists(zip_path):
            os.remove(zip_path)
        print("=== [완료: 기준 폴더 없음으로 중단] ===\n")
        send_result(token, userId, requestId, loginId, None, None, task)
        return

    total = len(file_list)
    totalPSNR = 0
    totalSSIM = 0
    psnr = None
    ssim = None
    valid_psnr_count = 0  # inf가 아닌 PSNR 값의 개수
    for idx, fname in enumerate(file_list, start=1):
        user_img_path = os.path.join(extract_file, fname)
        ref_img_path = os.path.join(reference_task_folder, fname)
        print(f"[{idx}/{total}] 파일명: {fname}")
        print(f" - 사용자 이미지: {user_img_path}")
        print(f" - 기준 이미지: {ref_img_path}")

        if os.path.exists(ref_img_path):
            try:
                user_img = cv2.imread(user_img_path)
                ref_img = cv2.imread(ref_img_path)
                if user_img is None or ref_img is None:
                    print(" - [경고] 이미지 파일을 읽지 못했습니다.")
                    psnr = None
                else:
                    psnr = calculate_psnr(user_img, ref_img)
                    ssim = calculate_ssim(user_img, ref_img)
                    print(f" - PSNR 계산값: {psnr}")
                    print(f" - SSIM 계산값: {ssim}")


            except Exception as e:
                psnr = None
                print(f" - [에러] PSNR 계산 중 에러: {e}")
            send_progress(token, userId, requestId, loginId, idx, total, fname, task)
            print(f" - 진행 상황 전송 (idx/total={idx}/{total})")
        else:
            send_progress(token, userId, requestId, loginId, None, None, None, task)
            print(" - [경고] 기준 이미지 없음, 진행 상황 전송 (psnr=None)")
        
        if psnr is not None:
            if math.isinf(psnr):
                totalPSNR += 100.0  # inf 대신 100을 사용 (매우 높은 PSNR 값)
            else:
                totalPSNR += psnr
            valid_psnr_count += 1

        if ssim is not None:
            totalSSIM += ssim

    # 유효한 PSNR 값이 있는 경우에만 평균 계산
    avgPSNR = totalPSNR / valid_psnr_count if valid_psnr_count > 0 else 0
    avgSSIM = totalSSIM / total

    # --- 최종 결과 메시지 전송 ---
    print(f"[5] 전체 결과 전송: 파일개수={total}, PSNR 결과 ={avgPSNR}, SSIM 결과={avgSSIM}")
    send_result(token, userId, requestId, loginId, avgPSNR, avgSSIM, task)

    # 파일 정리
    print(f"[6] 정리: 임시 폴더 삭제: {extract_path}")
    shutil.rmtree(extract_path)
    print(f"        zip 파일 삭제: {zip_path}")
    os.remove(zip_path)
    print("=== [완료] ===\n")


def run_consumer():
    print("==== [Kafka ZIP Consumer 기동] ====")

    # 카프카 연결 재시도 로직
    while True:
        try:
            consumer = KafkaConsumer(
                'compare.zip',
                bootstrap_servers='kafka:9092',
                value_deserializer=lambda m: m,  # 그대로 bytes 가져오기
                auto_offset_reset='earliest',
                enable_auto_commit=True,
                group_id='python-zip-consumer',
                max_partition_fetch_bytes=524288000 # 100MB
            )
            print("[Kafka 연결 성공]")
            break   # 연결 성공 시 루프 탈출
        except Exception as e:
            print(f"[Kafka 연결실패] {e} (3초 후 재시도)")
            time.sleep(3)

    print("max_partition_fetch_bytes:", consumer.config['max_partition_fetch_bytes'])
    print("카프카 consumer 대기 중...")

    for message in consumer:
        print("\n---- [새 메시지 도착!] ----")
        process_zip_message(message)

if __name__ == '__main__':
    run_consumer()
