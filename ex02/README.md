# MSA Microservices Project (ex02)

쿠버네티스 환경에서 실행되는 마이크로서비스 아키텍처 프로젝트입니다. Spring Boot 기반의 여러 서비스로 구성되어 있으며, API Gateway를 통한 통합 인증 및 라우팅을 제공합니다.

## 📋 프로젝트 개요

이 프로젝트는 다음 서비스들로 구성된 마이크로서비스 아키텍처입니다:

- **API Gateway** (포트: 8080): 모든 요청의 진입점, JWT 인증 및 라우팅
- **User Service** (포트: 8083): 사용자 관리 및 인증 (JWT 토큰 발급)
- **Order Service** (포트: 8081): 주문 관리
- **Product Service** (포트: 8082): 상품 관리 및 재고 관리
- **Delivery Service** (포트: 8084): 배송 관리
- **MySQL Database**: 공유 데이터베이스

## 🏗 아키텍처

```
                    ┌─────────────┐
                    │   Client    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │ (8080)
                    │  (JWT Auth) │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐      ┌──────▼──────┐    ┌─────▼─────┐
   │  User   │      │    Order    │    │  Product  │
   │ (8083)  │      │   (8081)    │    │  (8082)   │
   └────┬────┘      └──────┬──────┘    └─────┬─────┘
        │                  │                  │
        │            ┌─────▼─────┐            │
        │            │  Delivery │            │
        │            │   (8084)   │            │
        │            └─────┬──────┘            │
        │                  │                    │
        └──────────────────┼────────────────────┘
                           │
                    ┌──────▼──────┐
                    │   MySQL DB  │
                    └─────────────┘
```

## 🛠 기술 스택

### 공통
- **Java 21**
- **Spring Boot 3.5.5** (Gateway: 4.0.1)
- **Spring Data JPA**
- **MySQL 8.0**
- **JWT** (Auth0 java-jwt 4.3.0)
- **Lombok**
- **Gradle**

### 서비스별 추가 기술
- **API Gateway**: Spring MVC, RestTemplate
- **Order Service**: Spring Cloud OpenFeign (서비스 간 통신)
- **Product Service**: Spring Data JPA
- **User Service**: Spring Security (JWT 기반)
- **Delivery Service**: Spring Data JPA

## 📁 프로젝트 구조

```
ex02/
├── api-gateway/          # API Gateway 서비스
│   ├── src/main/java/com/metacoding/gateway/
│   │   ├── adapter/      # GatewayController
│   │   ├── service/      # GatewayService
│   │   └── core/         # JWT 필터, 설정
│   └── Dockerfile
│
├── user/                 # User 서비스 (인증)
│   ├── src/main/java/com/metacoding/user/
│   │   ├── adapter/      # Controller, Repository
│   │   ├── service/      # UserService
│   │   ├── domain/       # User 엔티티
│   │   └── core/         # JWT 필터, 설정
│   └── Dockerfile
│
├── order/                 # Order 서비스
│   ├── src/main/java/com/metacoding/order/
│   │   ├── adapter/      # Controller, FeignClient
│   │   ├── service/      # OrderService
│   │   ├── domain/       # Order 엔티티
│   │   └── core/         # JWT 필터, FeignConfig
│   └── Dockerfile
│
├── product/              # Product 서비스
│   ├── src/main/java/com/metacoding/product/
│   │   ├── adapter/      # Controller, Repository
│   │   ├── service/      # ProductService
│   │   ├── domain/       # Product 엔티티
│   │   └── core/         # JWT 필터, 설정
│   └── Dockerfile
│
├── delivery/             # Delivery 서비스
│   ├── src/main/java/com/metacoding/delivery/
│   │   ├── adapter/      # Controller, Repository
│   │   ├── service/      # DeliveryService
│   │   ├── domain/       # Delivery 엔티티
│   │   └── core/         # JWT 필터, 설정
│   └── Dockerfile
│
├── db/                   # 데이터베이스 초기화
│   ├── init.sql          # 스키마 및 초기 데이터
│   └── Dockerfile
│
└── k8s/                  # Kubernetes 배포 파일
    ├── gateway/          # Gateway 배포 설정
    ├── user/             # User 서비스 배포 설정
    ├── order/            # Order 서비스 배포 설정
    ├── product/          # Product 서비스 배포 설정
    ├── delivery/         # Delivery 서비스 배포 설정
    ├── db/               # DB 배포 설정
    └── README.md         # Kubernetes 배포 가이드
```

## 🚀 빠른 시작 가이드

### 사전 요구사항
- Docker Desktop (또는 Docker)
- Minikube
- kubectl
- Java 21
- Gradle

### Kubernetes 배포 (Minikube)

#### 1. Minikube 시작
```bash
minikube start
```

#### 2. 네임스페이스 생성
```bash
kubectl create namespace metacoding
```

#### 3. 이미지 빌드
```bash
# minikube의 Docker 데몬 사용 설정 (Git Bash)
eval $(minikube docker-env)

# 이미지 빌드
minikube image build -t metacoding/db:1 ./db
minikube image build -t metacoding/gateway:1 ./api-gateway
minikube image build -t metacoding/order:1 ./order
minikube image build -t metacoding/product:1 ./product
minikube image build -t metacoding/user:1 ./user
minikube image build -t metacoding/delivery:1 ./delivery
```

> **참고**: PowerShell이나 CMD에서는 `minikube docker-env` 출력을 확인하여 환경 변수를 설정하세요.

#### 4. Kubernetes 리소스 배포
```bash
# DB 먼저 배포 (필수)
kubectl apply -f k8s/db/

# 서비스 배포
kubectl apply -f k8s/gateway/
kubectl apply -f k8s/user/
kubectl apply -f k8s/order/
kubectl apply -f k8s/product/
kubectl apply -f k8s/delivery/
```

#### 5. 배포 상태 확인
```bash
# Pod 상태 확인
kubectl get pods -n metacoding

# Service 확인
kubectl get services -n metacoding

# 특정 서비스 로그 확인
kubectl logs -n metacoding -l app=gateway
```

#### 6. 서비스 접근
```bash
# 포트 포워딩
kubectl port-forward -n metacoding service/gateway-service 8080:8080

# 또는 Minikube Service URL
minikube service gateway-service -n metacoding --url
```

자세한 배포 가이드는 [k8s/README.md](k8s/README.md)를 참고하세요.

## 📡 API 사용 예시

### 1. 사용자 로그인 (JWT 토큰 발급)

```bash
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "ssar",
  "password": "1234"
}
```

**응답:**
```json
{
  "status": 200,
  "msg": "성공",
  "body": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9..."
}
```

### 2. 주문 생성 (인증 필요)

```bash
POST http://localhost:8080/api/orders
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "productId": 1,
  "quantity": 1
}
```

### 3. 상품 조회

```bash
GET http://localhost:8080/api/products/1?quantity=1
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...
```

## 🔐 인증 및 보안

### JWT 인증 흐름

1. **로그인**: `/login` 엔드포인트로 사용자 인증 후 JWT 토큰 발급
2. **토큰 검증**: API Gateway의 `JwtAuthenticationFilter`가 모든 요청의 JWT 토큰 검증
3. **사용자 ID 추출**: JWT에서 `userId` 추출하여 `X-User-Id` 헤더로 다운스트림 서비스에 전달
4. **서비스 간 통신**: Order 서비스가 Product/Delivery 서비스 호출 시 `X-User-Id` 헤더 자동 전달 (FeignConfig)

### 주요 보안 기능

- ✅ JWT 토큰 기반 인증
- ✅ 모든 API 엔드포인트에 인증 필터 적용
- ✅ `/login` 경로는 인증 제외
- ✅ 서비스 간 통신 시 사용자 ID 헤더 전달
- ✅ Kubernetes Secret을 통한 JWT 시크릿 키 관리

## 🔄 서비스 간 통신

### Order → Product/Delivery 통신

Order 서비스는 FeignClient를 사용하여 Product와 Delivery 서비스를 호출합니다:

- **FeignConfig**: 모든 FeignClient 요청에 `X-User-Id` 헤더 자동 추가
- **ProductClient**: 상품 조회 및 재고 감소
- **DeliveryClient**: 배송 생성

## 📊 데이터베이스

### 스키마
- **user_tb**: 사용자 정보
- **product_tb**: 상품 정보
- **order_tb**: 주문 정보
- **delivery_tb**: 배송 정보

### 초기 데이터
`db/init.sql`에 초기 스키마 및 테스트 데이터가 포함되어 있습니다.

## 🛠 로컬 개발

### 개별 서비스 실행

각 서비스는 독립적으로 실행 가능합니다:

```bash
# User 서비스
cd user
./gradlew bootRun

# Order 서비스
cd order
./gradlew bootRun

# Product 서비스
cd product
./gradlew bootRun

# Delivery 서비스
cd delivery
./gradlew bootRun

# API Gateway
cd api-gateway
./gradlew bootRun
```

### 프로파일
- **dev**: H2 인메모리 데이터베이스 사용
- **prod**: MySQL 데이터베이스 사용 (Kubernetes 환경)

## 📝 주요 기능

### API Gateway
- ✅ Path 기반 라우팅 (`/api/orders`, `/api/users`, `/api/products`, `/api/deliveries`)
- ✅ JWT 토큰 검증
- ✅ 사용자 ID를 `X-User-Id` 헤더로 전달
- ✅ 다운스트림 서비스로 요청 전달

### User Service
- ✅ 사용자 로그인 및 JWT 토큰 발급
- ✅ 사용자 정보 관리

### Order Service
- ✅ 주문 생성
- ✅ 주문 조회
- ✅ FeignClient를 통한 Product/Delivery 서비스 호출

### Product Service
- ✅ 상품 조회
- ✅ 재고 확인 및 감소
- ✅ 상품 목록 조회

### Delivery Service
- ✅ 배송 생성
- ✅ 배송 조회

## 🗑 리소스 삭제

```bash
# 애플리케이션 서비스 삭제
kubectl delete -f k8s/gateway/
kubectl delete -f k8s/order/
kubectl delete -f k8s/product/
kubectl delete -f k8s/user/
kubectl delete -f k8s/delivery/

# DB 삭제
kubectl delete -f k8s/db/

# 네임스페이스 삭제
kubectl delete namespace metacoding
```

















