#!/bin/bash

# 1. MongoDB Sink Connector 설치
VERSION="1.11.0"
TARGET_DIR="./custom-connectors/mongodb"
JAR_NAME="mongo-kafka-connect-${VERSION}-all.jar"
URL="https://search.maven.org/remotecontent?filepath=org/mongodb/kafka/mongo-kafka-connect/${VERSION}/${JAR_NAME}"

if [ ! -f "${TARGET_DIR}/${JAR_NAME}" ]; then
        echo "Installing MongoDB Sink Connector..."
        mkdir -p "${TARGET_DIR}"
        curl -L -o "${TARGET_DIR}/${JAR_NAME}" "${URL}"
else
        echo "✅ MongoDB Sink Connector already installed. Skipping..."
fi

# 2. Docker Composer 실행
echo "Starting Docker Compose services..."
docker compose up -d

# 3. Kafka 토픽 생성 (없는 경우에만)
function topic_exists() {
        docker exec kbroker kafka-topics --bootstrap-server localhost:9092 --list | grep -w "$1" > /dev/null 2>&1
}

CONNECT_TOPICS=("k_connect_configs" "k_connect_offsets" "k_connect_statuses")

for topic in "${CONNECT_TOPICS[@]}"; do
        if topic_exists "$topic"; then
                echo "✅ Topic $topic already exists. Skipping..."
        else
                echo "📌 Creating topic: $topic"
                docker exec kbroker kafka-topics \
                        --bootstrap-server localhost:9092 \
                        --create --topic "$topic" \
                        --partitions 3 --replication-factor 1 \
                        --config cleanup.policy=compact
        fi
done

HISTORY_TOPICS=("schemahistory.users")

for topic in "${HISTORY_TOPICS[@]}"; do
        if topic_exists "$topic"; then
                echo "✅ Topic $topic already exists. Skipping..."
        else
                echo "📌 Creating topic: $topic"
                docker exec kbroker kafka-topics \
                        --bootstrap-server localhost:9092 \
                        --create --topic "$topic" \
                        --partitions 3 --replication-factor 1 \
                        --config cleanup.policy=delete \
                        --config retention.ms=7776000000
        fi
done

DATA_TOPICS=("mysql.takeitdb.users")

for topic in "${DATA_TOPICS[@]}"; do
        if topic_exists "$topic"; then
                echo "✅ Topic $topic already exists. Skipping..."
        else
                echo "📌 Creating topic: $topic"
                docker exec kbroker kafka-topics \
                        --bootstrap-server localhost:9092 \
                        --create --topic "$topic" \
                        --partitions 3 --replication-factor 1
        fi
done

# 4. 카프카 커넥터 준비될 떄까지 대기
function wait_for_connect_ready() {
  local url="http://localhost:8083/connectors"
  local max_retries=30  # 최대 30번 (약 90초 대기)
  local count=0

  echo "⏳ Waiting for Kafka Connect to be ready..."

  while (( count < max_retries )); do
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    if [[ "$response" == "200" ]]; then
      echo "✅ Kafka Connect is ready!"
      return 0
    fi
    ((count++))
    echo "⌛ Waiting... ($count/$max_retries)"
    sleep 3
  done

  echo "❌ Kafka Connect did not become ready in time."
  return 1
}
wait_for_connect_ready

# 5. 커넥터 존재 여부 확인 함수 (jq 없이 grep 사용)
function connector_exists() {
  local connector_name="$1"
  curl -s http://localhost:8083/connectors | grep -o "\"${connector_name}\"" > /dev/null 2>&1
}

# 6. MySQL Source Connector 등록
if connector_exists "mysql-source-connector"; then
  echo "✅ mysql-source-connector already registered. Skipping..."
else
  echo "📌 Registering mysql-source-connector..."
  response=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    --data @scripts/mysql-source-connector.json \
    http://localhost:8083/connectors)

  if [ "$response" = "201" ]; then
    echo "✅ MySQL Source Connector 등록 성공"
  else
    echo "❌ 등록 실패 (HTTP 코드 $response). logs 확인 필요"
  fi
fi

# 7. MongoDB Sink Connector 등록
if connector_exists "mongo-sink-connector"; then
  echo "✅ mongo-sink-connector already registered. Skipping..."
else
  echo "📌 Registering mongo-sink-connector..."
  response=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    --data @scripts/mongodb-sink-connector.json \
    http://localhost:8083/connectors)

  if [ "$response" = "201" ]; then
    echo "✅ MongoDB Sink Connector 등록 성공"
  else
    echo "❌ 등록 실패 (HTTP 코드 $response). logs 확인 필요"
  fi
fi