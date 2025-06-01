import os
import zipfile
import base64
import json
import shutil
from kafka import KafkaConsumer
from mykafka.taskproducer import send_status_message


TEMP_FOLDER = './static/reference'
os.makedirs(TEMP_FOLDER, exist_ok=True)

def process_zip_message(message):
    print("\n=== [process_zip_message: 메시지 도착] ===")
    try:
        msg_text = message.value.decode()
        print(f"수신 원본(디코딩 후): {msg_text[:200]}...")
        msg = json.loads(msg_text)
    except Exception as e:
        print(f"[에러] JSON 디코드 실패: {e}")
        send_status_message(token, userId, requestId, loginId, task, "success")
        return

    token = msg.get('token')
    userId = msg.get('userId')
    loginId = msg.get('loginId')
    requestId = msg.get('requestId')
    task = msg.get('task')
    base64zip = msg.get('zipFile')

    print(f"task: {task}")
    print(f"base64zip: 앞 100자: {str(base64zip)[:100]} ...")

    try:
        # 1. 압축파일 저장
        zip_bytes = base64.b64decode(base64zip)
        extract_path = os.path.join(TEMP_FOLDER, task)
        os.makedirs(extract_path, exist_ok=True)
        zip_filename = os.path.join(extract_path, f"{task}.zip")
        with open(zip_filename, "wb") as f:
            f.write(zip_bytes)
        print(f"[1] zip 파일 저장 완료: {zip_filename} ({len(zip_bytes)} bytes)")

        # 2. 압축 해제 (임시 폴더 생성)
        tmp_extract_dir = os.path.join(extract_path, "_tmp")
        os.makedirs(tmp_extract_dir, exist_ok=True)
        with zipfile.ZipFile(zip_filename, 'r') as zip_ref:
            zip_ref.extractall(tmp_extract_dir)
        print(f"[2] 압축 임시 해제 완료. 해제 경로: {tmp_extract_dir}")

        # 3. result 폴더 내 파일만 extract_path 바로 아래로 이동
        result_dir = os.path.join(tmp_extract_dir, 'result')
        if os.path.isdir(result_dir):
            moved = 0
            for fname in os.listdir(result_dir):
                src_file = os.path.join(result_dir, fname)
                dst_file = os.path.join(extract_path, fname)
                if os.path.isfile(src_file):
                    shutil.move(src_file, dst_file)
                    moved += 1
            print(f"[3] {moved}개 파일 이동 완료: {extract_path}")
        else:
            print(f"[경고] result 폴더가 없습니다: {result_dir}")

        # 4. 임시 해제 폴더 및 압축파일 삭제
        shutil.rmtree(tmp_extract_dir)
        os.remove(zip_filename)
        print(f"[4] 임시폴더 및 zip 파일 정리 완료.")
        
        print(f"[최종 폴더] {extract_path} : {os.listdir(extract_path)}")

        # 5. 완료 메시지 전송
        send_status_message(token, userId, requestId, loginId, task, "success")
        
    except Exception as e:
        print(f"[에러] 압축 해제 중 에러: {e}")
        send_status_message(token, userId, requestId, loginId, task, "fail")

def run_consumer():
    print("==== [Kafka ZIP Consumer 기동] ====")
    while True:
        try:
            consumer = KafkaConsumer(
                'task.compare.zip',
                bootstrap_servers='kafka:9092',
                value_deserializer=lambda m: m,
                auto_offset_reset='earliest',
                enable_auto_commit=True,
                group_id='task-zip-consumer',
                max_partition_fetch_bytes=524288000
            )
            print("[Kafka 연결 성공]")
            break
        except Exception as e:
            print(f"[Kafka 연결실패] {e} (3초 후 재시도)")
            import time
            time.sleep(3)

    print("Kafka consumer 대기 중...")

    for message in consumer:
        print("\n---- [새 메시지 도착!] ----")
        process_zip_message(message)

if __name__ == '__main__':
    run_consumer()
