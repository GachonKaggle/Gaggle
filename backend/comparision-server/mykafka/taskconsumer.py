import os  # Provides functions for interacting with the operating system
import zipfile  # Used to extract ZIP files
import base64  # Used to decode base64-encoded strings
import json  # Used to parse JSON strings
import shutil  # Used for file operations such as deleting directories or moving files
from kafka import KafkaConsumer  # Kafka client to consume messages from Kafka topics
from mykafka.taskproducer import send_status_message  # Custom Kafka message sender

# Define the folder where extracted ZIP contents will be stored
TEMP_FOLDER = './static/reference'
os.makedirs(TEMP_FOLDER, exist_ok=True)  # Create the folder if it doesn't exist

def process_zip_message(message):
    print("\n=== [process_zip_message: 메시지 도착] ===")
    try:
        # Decode the Kafka message from bytes to string
        msg_text = message.value.decode()
        print(f"수신 원본(디코딩 후): {msg_text[:200]}...")
        # Parse JSON from the message string
        msg = json.loads(msg_text)
    except Exception as e:
        # If decoding fails, log the error and send failure status
        print(f"[에러] JSON 디코드 실패: {e}")
        send_status_message(token, userId, requestId, loginId, task, "success")
        return

    # Extract values from JSON payload
    token = msg.get('token')
    userId = msg.get('userId')
    loginId = msg.get('loginId')
    requestId = msg.get('requestId')
    task = msg.get('task')
    base64zip = msg.get('zipFile')

    print(f"task: {task}")
    print(f"base64zip: 앞 100자: {str(base64zip)[:100]} ...")

    try:
        # Step 1: Save base64-decoded ZIP file to disk
        zip_bytes = base64.b64decode(base64zip)  # Decode base64 string into bytes
        extract_path = os.path.join(TEMP_FOLDER, task)  # Path where the task files will be extracted
        os.makedirs(extract_path, exist_ok=True)  # Create the directory if it doesn't exist
        zip_filename = os.path.join(extract_path, f"{task}.zip")  # Full path to save the ZIP file
        with open(zip_filename, "wb") as f:
            f.write(zip_bytes)  # Write ZIP bytes to file
        print(f"[1] zip 파일 저장 완료: {zip_filename} ({len(zip_bytes)} bytes)")

        # Step 2: Extract ZIP file contents to a temporary folder
        tmp_extract_dir = os.path.join(extract_path, "_tmp")  # Path for temporary extraction
        os.makedirs(tmp_extract_dir, exist_ok=True)  # Ensure the temp dir exists
        with zipfile.ZipFile(zip_filename, 'r') as zip_ref:
            zip_ref.extractall(tmp_extract_dir)  # Extract all files
        print(f"[2] 압축 임시 해제 완료. 해제 경로: {tmp_extract_dir}")

        # Step 3: Move files from result folder to final destination
        result_dir = os.path.join(tmp_extract_dir, 'result')  # Path to result subfolder
        if os.path.isdir(result_dir):  # If result folder exists
            moved = 0  # Counter for moved files
            for fname in os.listdir(result_dir):  # Iterate over result folder files
                src_file = os.path.join(result_dir, fname)  # Source file path
                dst_file = os.path.join(extract_path, fname)  # Destination file path
                if os.path.isfile(src_file):  # If it's a file (not folder)
                    shutil.move(src_file, dst_file)  # Move the file
                    moved += 1
            print(f"[3] {moved}개 파일 이동 완료: {extract_path}")
        else:
            print(f"[경고] result 폴더가 없습니다: {result_dir}")

        # Step 4: Cleanup temporary files
        shutil.rmtree(tmp_extract_dir)  # Delete temporary extraction folder
        os.remove(zip_filename)  # Delete original ZIP file
        print(f"[4] 임시폴더 및 zip 파일 정리 완료.")
        print(f"[최종 폴더] {extract_path} : {os.listdir(extract_path)}")

        # Step 5: Send success message via Kafka
        send_status_message(token, userId, requestId, loginId, task, "success")
        
    except Exception as e:
        # Handle any exceptions during the extraction/move process
        print(f"[에러] 압축 해제 중 에러: {e}")
        send_status_message(token, userId, requestId, loginId, task, "fail")

def run_consumer():
    print("==== [Kafka ZIP Consumer 기동] ====")
    while True:
        try:
            # Try to connect to Kafka
            consumer = KafkaConsumer(
                'task.compare.zip',  # Kafka topic to subscribe to
                bootstrap_servers='kafka:9092',  # Kafka broker address
                value_deserializer=lambda m: m,  # Read raw bytes
                auto_offset_reset='earliest',  # Start from earliest offset if no commit
                enable_auto_commit=True,  # Automatically commit read offset
                group_id='task-zip-consumer',  # Consumer group ID
                max_partition_fetch_bytes=524288000  # Max fetch size: 500MB
            )
            print("[Kafka 연결 성공]")
            break  # Exit loop on successful connection
        except Exception as e:
            # Retry connection on failure
            print(f"[Kafka 연결실패] {e} (3초 후 재시도)")
            import time
            time.sleep(3)

    print("Kafka consumer 대기 중...")

    # Continuously listen for new messages
    for message in consumer:
        print("\n---- [새 메시지 도착!] ----")
        process_zip_message(message)  # Process each received message

# Main entry point of script
if __name__ == '__main__':
    run_consumer()
