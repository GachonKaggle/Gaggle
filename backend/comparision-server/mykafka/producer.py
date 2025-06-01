from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers="kafka:9092",
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def send_progress(token, user_id, request_id, loginId, current, total, filename, task):
    msg = {
        "token": token,
        "userId": user_id,
        "requestId": request_id,
        "loginId": loginId,
        "current": current,
        "total": total,
        "filename": filename,
        "task": task,
    }
    producer.send('psnr.progress', value=msg)

def send_result(token, user_id, request_id, loginId, psnrAvg, ssimAvg, task):
    msg = {
        "token": token,
        "userId": user_id,
        "requestId": request_id,
        "loginId": loginId,
        "psnrAvg": psnrAvg,
        "ssimAvg" : ssimAvg,
        "task": task,
    }
    producer.send('psnr.results', value=msg)
    producer.flush()
