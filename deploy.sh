#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "${SCRIPT_DIR}/.deploy.env" ]; then
  echo ".deploy.env 파일이 없습니다." >&2
  exit 1
fi

source "${SCRIPT_DIR}/.deploy.env"

IMAGE_NAME="daily-record-api"
IMAGE_TAG="latest"
IMAGE_FILE="${IMAGE_NAME}.tar"

echo "=== 1. Docker 이미지 빌드 (linux/arm64) ==="
docker build --platform linux/arm64 -t "${IMAGE_NAME}:${IMAGE_TAG}" .

echo "=== 2. Docker 이미지 저장 ==="
docker save -o "${IMAGE_FILE}" "${IMAGE_NAME}:${IMAGE_TAG}"

echo "=== 3. 라즈베리파이로 이미지 전송 ==="
scp "${IMAGE_FILE}" "${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/${IMAGE_FILE}"

echo "=== 4. 로컬 이미지 파일 정리 ==="
rm -f "${IMAGE_FILE}"

echo "=== 5. 원격 배포 실행 ==="
ssh "${REMOTE_USER}@${REMOTE_HOST}" "cd ${REMOTE_DIR} && bash deploy.sh"

echo "=== 배포 완료 ==="
