@echo off
REM 미니큐브 배포 스크립트 (Windows)

echo === 미니큐브 배포 시작 ===

REM 1. 미니큐브가 실행 중인지 확인
minikube status >nul 2>&1
if errorlevel 1 (
    echo 미니큐브가 실행 중이 아닙니다. 미니큐브를 시작합니다...
    minikube start
)

REM 2. 미니큐브의 도커 데몬 사용 설정
echo 미니큐브 도커 데몬 설정 중...
@FOR /f "tokens=*" %i IN ('minikube docker-env') DO @%i

REM 3. 네임스페이스 생성
echo 네임스페이스 생성 중...
kubectl create namespace metacoding --dry-run=client -o yaml | kubectl apply -f -

REM 4. 이미지 빌드
echo 이미지 빌드 중...
docker build -t metacoding/gateway:1 ./api-gateway
docker build -t metacoding/order:1 ./order
docker build -t metacoding/product:1 ./product
docker build -t metacoding/user:1 ./user
docker build -t metacoding/delivery:1 ./delivery

REM 5. Kubernetes 리소스 배포
echo Kubernetes 리소스 배포 중...
kubectl apply -f k8s/gateway/
kubectl apply -f k8s/order/
kubectl apply -f k8s/product/
kubectl apply -f k8s/user/
kubectl apply -f k8s/delivery/

REM 6. 배포 상태 확인
echo 배포 상태 확인 중...
kubectl get pods -n metacoding
kubectl get services -n metacoding

echo.
echo === 배포 완료 ===
echo 서비스 접근 방법:
echo   minikube service gateway-service -n metacoding
echo.
echo 또는 포트 포워딩:
echo   kubectl port-forward -n metacoding service/gateway-service 8080:8080





















