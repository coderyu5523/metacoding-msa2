#!/bin/bash

# 미니큐브 배포 스크립트

set -e

echo "=== 미니큐브 배포 시작 ==="

# 1. 미니큐브가 실행 중인지 확인
if ! minikube status > /dev/null 2>&1; then
    echo "미니큐브가 실행 중이 아닙니다. 미니큐브를 시작합니다..."
    minikube start
fi

# 2. 미니큐브의 도커 데몬 사용 설정
echo "미니큐브 도커 데몬 설정 중..."
eval $(minikube docker-env)

# 3. 네임스페이스 생성
echo "네임스페이스 생성 중..."
kubectl create namespace metacoding --dry-run=client -o yaml | kubectl apply -f -

# 4. 이미지 빌드
echo "이미지 빌드 중..."
docker build -t metacoding/gateway:1 ./api-gateway
docker build -t metacoding/order:1 ./order
docker build -t metacoding/product:1 ./product
docker build -t metacoding/user:1 ./user
docker build -t metacoding/delivery:1 ./delivery

# 5. Kubernetes 리소스 배포
echo "Kubernetes 리소스 배포 중..."
kubectl apply -f k8s/gateway/
kubectl apply -f k8s/order/
kubectl apply -f k8s/product/
kubectl apply -f k8s/user/
kubectl apply -f k8s/delivery/

# 6. 배포 상태 확인
echo "배포 상태 확인 중..."
kubectl get pods -n metacoding
kubectl get services -n metacoding

echo ""
echo "=== 배포 완료 ==="
echo "서비스 접근 방법:"
echo "  minikube service gateway-service -n metacoding"
echo ""
echo "또는 포트 포워딩:"
echo "  kubectl port-forward -n metacoding service/gateway-service 8080:8080"
























