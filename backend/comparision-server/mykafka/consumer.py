import os  # Provides functions to interact with the operating system (e.g., file paths)
import time  # Provides time-related functions (e.g., sleep)
import zipfile  # Provides tools to work with ZIP archives
import uuid  # For generating universally unique identifiers
import shutil  # Provides high-level file operations (e.g., copying, removal)
import cv2  # OpenCV library for image processing
import math  # Provides mathematical functions (e.g., isinf)

from kafka import KafkaConsumer  # Kafka library to consume messages
from mykafka.producer import send_progress, send_result  # Custom Kafka producer functions for progress and result
from utils import calculate_psnr, calculate_ssim  # Custom utilities for image comparison metrics

# Define directory paths for uploads, temp files, and reference data
UPLOAD_FOLDER = './uploads'
TEMP_FOLDER = './temp'
REFERENCE_FOLDER = './static/reference'

# Ensure upload and temp folders exist
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(TEMP_FOLDER, exist_ok=True)

# Main function to process incoming Kafka message containing zip file
def process_zip_message(message):
    import base64  # For decoding base64 strings
    import json  # For parsing JSON format

    print("\n=== [1] process_zip_message: 메시지 도착 ===")
    try:
        msg_text = message.value.decode()  # Decode message bytes to string
        print(f"수신 원본(디코딩 후): {msg_text[:200]}...")  # Preview the message
        msg = json.loads(msg_text)  # Parse JSON string into dictionary
    except Exception as e:
        print(f"[에러] JSON 디코드 실패: {e}")  # JSON decode failed
        send_result(userId, requestId, loginId, None, None, task)  # Notify failure
        return

    # Extract fields from JSON message
    token = msg.get('token')
    userId = msg.get('userId')
    loginId = msg.get('loginId')
    requestId = msg.get('requestId') or str(uuid.uuid4())  # Generate new requestId if not provided
    task = msg.get('task')
    base64zip = msg.get('zipFile')  # The uploaded file in base64 format

    # Print extracted information
    print(f"token: {token}")
    print(f"userId: {userId}")
    print(f"loginId: {loginId}")
    print(f"requestId: {requestId}")
    print(f"task: {task}")
    print(f"base64zip: 앞 100자: {str(base64zip)[:100]} ...")

    # Save base64-encoded zip file to disk
    try:
        zip_bytes = base64.b64decode(base64zip)  # Decode base64 to bytes
        zip_filename = f"{loginId}_{requestId}.zip"  # Create zip file name
        zip_path = os.path.join(UPLOAD_FOLDER, zip_filename)  # Full path to save file
        with open(zip_path, "wb") as f:
            f.write(zip_bytes)  # Write binary data to file
        print(f"[2] zip 파일 저장 완료: {zip_path} ({len(zip_bytes)} bytes)")
    except Exception as e:
        print(f"[에러] zip 파일 저장 실패: {e}")
        send_result(token, userId, requestId, loginId, None, None, task)
        return

    # Extract zip file to a new directory
    extract_path = os.path.join(TEMP_FOLDER, os.path.splitext(zip_filename)[0])
    try:
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            zip_ref.extractall(extract_path)  # Extract all files in the zip
        print(f"[3] 압축 해제 완료. 해제 경로: {extract_path}")
        print(f"[3-1] 압축 파일 개수: {os.listdir(extract_path)}")
    except Exception as e:
        print(f"[에러] 압축 해제 실패: {e}")
        send_result(token,userId, requestId, loginId, None, None, task)
        return

    # List image files in extracted 'result' folder
    extract_file = os.path.join(extract_path, "result")
    try:
        file_list = sorted([f for f in os.listdir(extract_file) if os.path.isfile(os.path.join(extract_file, f))])
    except Exception as e:
        print(f"[에러] 압축 내 'result' 폴더 없음 또는 파일 목록 조회 실패: {e}")
        if os.path.exists(extract_path):
            shutil.rmtree(extract_path)  # Delete extracted folder
        if os.path.exists(zip_path):
            os.remove(zip_path)  # Delete zip file
        print("=== [완료: result 폴더 없음으로 중단] ===\n")
        send_result(token, userId, requestId, loginId, None, None, task)
        return

    # Path to the corresponding reference image folder for the task
    reference_task_folder = os.path.join(REFERENCE_FOLDER, task)
    if not os.path.exists(reference_task_folder):
        error_msg = f"[에러] 기준 폴더 없음: {reference_task_folder}"
        print(error_msg)
        if os.path.exists(extract_path):
            shutil.rmtree(extract_path)
        if os.path.exists(zip_path):
            os.remove(zip_path)
        print("=== [완료: 기준 폴더 없음으로 중단] ===\n")
        send_result(token, userId, requestId, loginId, None, None, task)
        return

    # Initialize stats
    total = len(file_list)  # Number of images
    totalPSNR = 0  # Accumulated PSNR
    totalSSIM = 0  # Accumulated SSIM
    psnr = None
    ssim = None
    valid_psnr_count = 0  # Counter for valid (non-inf) PSNR

    # Compare each file
    for idx, fname in enumerate(file_list, start=1):
        user_img_path = os.path.join(extract_file, fname)  # Path to user image
        ref_img_path = os.path.join(reference_task_folder, fname)  # Path to reference image
        print(f"[{idx}/{total}] 파일명: {fname}")
        print(f" - 사용자 이미지: {user_img_path}")
        print(f" - 기준 이미지: {ref_img_path}")

        if os.path.exists(ref_img_path):
            try:
                user_img = cv2.imread(user_img_path)  # Load user image
                ref_img = cv2.imread(ref_img_path)  # Load reference image
                if user_img is None or ref_img is None:
                    print(" - [경고] 이미지 파일을 읽지 못했습니다.")
                    psnr = None
                else:
                    psnr = calculate_psnr(user_img, ref_img)  # Compute PSNR
                    ssim = calculate_ssim(user_img, ref_img)  # Compute SSIM
                    print(f" - PSNR 계산값: {psnr}")
                    print(f" - SSIM 계산값: {ssim}")
            except Exception as e:
                psnr = None
                print(f" - [에러] PSNR 계산 중 에러: {e}")
            send_progress(token, userId, requestId, loginId, idx, total, fname, task)  # Send progress update
            print(f" - 진행 상황 전송 (idx/total={idx}/{total})")
        else:
            send_progress(token, userId, requestId, loginId, None, None, None, task)
            print(" - [경고] 기준 이미지 없음, 진행 상황 전송 (psnr=None)")

        # Accumulate stats only if PSNR is valid
        if psnr is not None:
            if math.isinf(psnr):
                totalPSNR += 100.0  # Treat infinity as 100
            else:
                totalPSNR += psnr
            valid_psnr_count += 1

        if ssim is not None:
            totalSSIM += ssim

    # Final average calculation
    avgPSNR = totalPSNR / valid_psnr_count if valid_psnr_count > 0 else 0
    avgSSIM = totalSSIM / total

    # Send final result
    print(f"[5] 전체 결과 전송: 파일개수={total}, PSNR 결과 ={avgPSNR}, SSIM 결과={avgSSIM}")
    send_result(token, userId, requestId, loginId, avgPSNR, avgSSIM, task)

    # Clean up extracted and zip files
    print(f"[6] 정리: 임시 폴더 삭제: {extract_path}")
    shutil.rmtree(extract_path)
    print(f"        zip 파일 삭제: {zip_path}")
    os.remove(zip_path)
    print("=== [완료] ===\n")

# Run the Kafka consumer that listens to 'compare.zip' topic
def run_consumer():
    print("==== [Kafka ZIP Consumer 기동] ====")

    # Retry logic for Kafka connection
    while True:
        try:
            consumer = KafkaConsumer(
                'compare.zip',  # Topic name
                bootstrap_servers='kafka:9092',  # Kafka server
                value_deserializer=lambda m: m,  # Use raw bytes
                auto_offset_reset='earliest',  # Start from earliest if no offset
                enable_auto_commit=True,  # Commit offsets automatically
                group_id='python-zip-consumer',  # Consumer group ID
                max_partition_fetch_bytes=524288000  # Max fetch size (100MB)
            )
            print("[Kafka 연결 성공]")
            break
        except Exception as e:
            print(f"[Kafka 연결실패] {e} (3초 후 재시도)")
            time.sleep(3)

    print("max_partition_fetch_bytes:", consumer.config['max_partition_fetch_bytes'])
    print("카프카 consumer 대기 중...")

    # Infinite loop to consume messages
    for message in consumer:
        print("\n---- [새 메시지 도착!] ----")
        process_zip_message(message)

# Entry point
if __name__ == '__main__':
    run_consumer()
