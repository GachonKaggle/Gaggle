from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers="kafka:9092",
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def send_status_message(token, user_id, request_id, loginId, task, status):
    msg = {
        "token": token,
        "userId": user_id,
        "requestId": request_id,
        "loginId": loginId,
        "task": task,
        "status": status 
    }
    producer.send('task.status', value=msg)
    producer.flush()

