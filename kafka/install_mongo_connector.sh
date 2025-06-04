#!/bin/bash

VERSION="1.11.0"
TARGET_DIR="../custom-connectors/mongodb"
JAR_NAME="mongo-kafka-connect-${VERSION}-all.jar"
URL="https://search.maven.org/remotecontent?filepath=org/mongodb/kafka/mongo-kafka-connect/${VERSION}/${JAR_NAME}"

mkdir -p "${TARGET_DIR}"

echo "🔄 Downloading MongoDB Sink Connector v${VERSION}..."
curl -L -o "${TARGET_DIR}/${JAR_NAME}" "${URL}"

if [ -f "${TARGET_DIR}/${JAR_NAME}" ]; then
  echo "✅ 다운로드 완료: ${TARGET_DIR}/${JAR_NAME}"
else
  echo "❌ 다운로드 실패"
fi
