FROM python:3.9-slim

WORKDIR /comparision-server

# OpenCV를 위해 필요한 시스템 패키지 설치 (libGL, glib, 등)
RUN apt-get update && \
    apt-get install -y \
        libgl1-mesa-glx \
        libglib2.0-0 \
        libsm6 \
        libxrender1 \
        libxext6 && \
    rm -rf /var/lib/apt/lists/*

# 의존성 설치
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# 소스코드 복사
COPY . .

# 앱 실행 (예: Flask)
CMD ["sh", "-c", "python -u -m mykafka.consumer & python -u -m mykafka.taskconsumer & wait"]
