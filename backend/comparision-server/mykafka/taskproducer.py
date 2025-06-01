from kafka import KafkaProducer  # Import KafkaProducer from kafka module
import json  # Import JSON module to handle JSON serialization

# Create a Kafka producer that connects to the Kafka broker at kafka:9092
# The value_serializer converts Python dicts to JSON-encoded UTF-8 bytes
producer = KafkaProducer(
    bootstrap_servers="kafka:9092",
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# Function to send a task status message to the Kafka topic 'task.status'
def send_status_message(token, user_id, request_id, loginId, task, status):
    # Construct the message payload as a dictionary
    msg = {
        "token": token,              # Authentication token
        "userId": user_id,           # User identifier
        "requestId": request_id,     # Unique request ID
        "loginId": loginId,          # User login ID
        "task": task,                # Task name or type
        "status": status             # Task status (e.g., "success", "fail")
    }
    # Send the message to the 'task.status' topic
    producer.send('task.status', value=msg)
    # Ensure the message is actually sent by flushing the producer buffer
    producer.flush()
