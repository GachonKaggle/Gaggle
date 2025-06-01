from kafka import KafkaProducer  # Kafka producer to send messages to Kafka topics
import json  # Used to serialize messages to JSON format

# Create a KafkaProducer instance configured to serialize JSON messages
producer = KafkaProducer(
    bootstrap_servers="kafka:9092",  # Kafka broker address
    value_serializer=lambda v: json.dumps(v).encode('utf-8')  # Serialize value as UTF-8 encoded JSON
)

# Function to send progress updates (e.g., during image comparisons)
def send_progress(token, user_id, request_id, loginId, current, total, filename, task):
    msg = {
        "token": token,  # Auth token for identifying the user/session
        "userId": user_id,  # User identifier
        "requestId": request_id,  # Unique request identifier
        "loginId": loginId,  # Login ID of the user
        "current": current,  # Current image index being processed
        "total": total,  # Total number of images
        "filename": filename,  # Name of the image being processed
        "task": task,  # Task identifier (e.g., type of comparison)
    }
    producer.send('psnr.progress', value=msg)  # Send message to the 'psnr.progress' topic

# Function to send final result after processing all images
def send_result(token, user_id, request_id, loginId, psnrAvg, ssimAvg, task):
    msg = {
        "token": token,  # Auth token
        "userId": user_id,  # User ID
        "requestId": request_id,  # Request ID
        "loginId": loginId,  # Login ID
        "psnrAvg": psnrAvg,  # Average PSNR score
        "ssimAvg" : ssimAvg,  # Average SSIM score
        "task": task,  # Task type
    }
    producer.send('psnr.results', value=msg)  # Send message to the 'psnr.results' topic
    producer.flush()  # Ensure all buffered records are sent immediately
