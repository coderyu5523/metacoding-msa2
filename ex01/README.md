# MSA 주문 시스템 (ex01)

마이크로서비스 아키텍처 기반의 주문 관리 시스템입니다.

## 📋 프로젝트 개요

이 프로젝트는 4개의 독립적인 마이크로서비스로 구성된 주문 관리 시스템입니다:
- **User Service**: 사용자 인증 및 관리
- **Product Service**: 상품 관리 및 재고 관리
- **Order Service**: 주문 처리 및 오케스트레이션
- **Delivery Service**: 배달 관리

## 🏗️ 아키텍처

```
┌─────────────┐
│ User Service│ (8083)
│  - 로그인    │
│  - 사용자 조회│
└─────────────┘

┌─────────────┐
│Product      │ (8082)
│Service      │
│  - 상품 조회 │
│  - 재고 관리 │
└─────────────┘
      ↑
      │ Feign Client
      │
┌─────────────┐
│Order Service│ (8081) ──┐
│  - 주문 생성 │          │
│  - 주문 조회 │          │ Feign Client
└─────────────┘          │
      │                  ↓
      │          ┌─────────────┐
      │          │Delivery     │ (8084)
      └──────────│Service      │
                 │  - 배달 생성 │
                 │  - 배달 조회 │
                 └─────────────┘
```

## 🛠️ 기술 스택

### 공통 기술
- **Java**: 21
- **Spring Boot**: 3.2.9 (order), 3.5.5 (기타 서비스)
- **Spring Data JPA**: 데이터베이스 접근
- **H2 Database**: 인메모리 데이터베이스
- **Lombok**: 보일러플레이트 코드 제거
- **JWT**: 인증 (com.auth0:java-jwt:4.3.0)

### 서비스별 추가 기술
- **Order Service**
  - Spring Cloud OpenFeign: 서비스 간 통신
  - JWT 인증 필터

- **User Service**
  - JWT 토큰 생성

- **Delivery Service**
  - JWT 인증 필터

- **Product Service**
  - JWT 인증 필터

## 📦 서비스 상세

### 1. User Service (Port: 8083)
**역할**: 사용자 인증 및 관리

**주요 기능**:
- 사용자 로그인 (JWT 토큰 발급)
- 사용자 조회 (단일/전체)

**API 엔드포인트**:
- `POST /login` - 로그인
- `GET /api/users/{userId}` - 사용자 조회
- `GET /api/users` - 전체 사용자 조회

**데이터베이스**:
- 테이블: `user_tb`
- 필드: id, username, email, password

### 2. Product Service (Port: 8082)
**역할**: 상품 관리 및 재고 관리

**주요 기능**:
- 상품 조회 (재고 확인 포함)
- 상품 목록 조회
- 재고 감소 (상태 관리: PENDING → SUCCESS)

**API 엔드포인트**:
- `GET /api/products/{productId}?quantity={quantity}` - 상품 조회 (재고 확인)
- `GET /api/products` - 전체 상품 조회
- `POST /api/products/{productId}/decrease?quantity={quantity}` - 재고 감소

**데이터베이스**:
- 테이블: `product_tb`
- 필드: id, product_name, quantity, price, status

**상태 관리**:
- 재고 감소 시: `PENDING` → `SUCCESS` (자동 업데이트)

### 3. Order Service (Port: 8081)
**역할**: 주문 처리 및 오케스트레이션

**주요 기능**:
- 주문 생성 (다른 서비스들과 통신하여 처리)
- 주문 조회
- JWT 인증 기반 사용자 식별

**API 엔드포인트**:
- `POST /api/orders` - 주문 생성 (JWT 인증 필요)
- `GET /api/orders/{orderId}` - 주문 조회

**주문 처리 흐름**:
1. 주문 생성 (PENDING 상태)
2. 상품 재고 확인 (Product Service)
3. 상품 재고 감소 (Product Service)
4. 배달 생성 (Delivery Service)
5. 주문 상태를 "success"로 변경

**Feign Client**:
- `ProductClient`: Product Service와 통신
- `DeliveryClient`: Delivery Service와 통신

**데이터베이스**:
- 테이블: `order_tb`
- 필드: id, user_id, product_id, quantity, status, created_at, updated_at

### 4. Delivery Service (Port: 8084)
**역할**: 배달 관리

**주요 기능**:
- 배달 생성 (자동으로 "success" 상태로 설정)
- 배달 조회

**API 엔드포인트**:
- `POST /api/deliveries` - 배달 생성
- `GET /api/deliveries/{id}` - 배달 조회

**데이터베이스**:
- 테이블: `delivery_tb`
- 필드: id, order_id, address, status, created_at, updated_at

**상태 관리**:
- 배달 생성 시: 자동으로 "success" 상태로 설정

## 🔐 인증 및 보안

- **JWT 기반 인증**: 모든 서비스에서 JWT 토큰 사용
- **인증 필터**: Order, Product, Delivery 서비스에 JwtAuthenticationFilter 적용
- **토큰 생성**: User Service의 `/login` 엔드포인트에서 토큰 발급

## 🚀 실행 방법

### Docker Compose를 사용한 실행

```bash
docker-compose up 
```

### 개별 서비스 실행

각 서비스 디렉토리에서:

```bash
./gradlew bootRun
```

## 📡 서비스 간 통신

### Order Service → Product Service
- **통신 방식**: Spring Cloud OpenFeign
- **URL**: `http://product-service:8082`
- **주요 호출**:
  - 상품 조회 및 재고 확인
  - 재고 감소

### Order Service → Delivery Service
- **통신 방식**: Spring Cloud OpenFeign
- **URL**: `http://delivery-service:8084`
- **주요 호출**:
  - 배달 생성

## 📊 데이터베이스

모든 서비스는 **H2 인메모리 데이터베이스**를 사용합니다.

### 초기 데이터

각 서비스의 `src/main/resources/db/data.sql`에 초기 데이터가 포함되어 있습니다:

- **User Service**: 3명의 사용자 (ssar, cos, love)
- **Product Service**: 3개의 상품 (MacBook Pro, iPhone 15, AirPods)
- **Order Service**: 3개의 주문 데이터
- **Delivery Service**: 3개의 배달 데이터

### H2 Console 접근

각 서비스의 H2 Console에 접근 가능:
- `http://localhost:8081/h2-console` (Order Service)
- `http://localhost:8082/h2-console` (Product Service)
- `http://localhost:8083/h2-console` (User Service)
- `http://localhost:8084/h2-console` (Delivery Service)

## 🔄 주문 처리 플로우

```
1. 클라이언트 → Order Service: 주문 요청 (JWT 토큰 포함)
   ↓
2. Order Service: JWT 검증 및 사용자 ID 추출
   ↓
3. Order Service: 주문 생성 (PENDING 상태)
   ↓
4. Order Service → Product Service: 상품 재고 확인
   ↓
5. Order Service → Product Service: 재고 감소
   (Product Service: PENDING → SUCCESS 상태 변경)
   ↓
6. Order Service → Delivery Service: 배달 생성
   (Delivery Service: 자동으로 success 상태로 설정)
   ↓
7. Order Service: 주문 상태를 "success"로 변경
   ↓
8. Order Service → 클라이언트: 주문 완료 응답
```

## 📝 주요 특징

1. **마이크로서비스 아키텍처**: 각 서비스가 독립적으로 배포 가능
2. **서비스 간 통신**: OpenFeign을 통한 동기 통신
3. **상태 관리**: Product와 Delivery 서비스에서 상태 자동 관리
4. **JWT 인증**: 사용자 인증 및 권한 관리
5. **트랜잭션 관리**: 각 서비스에서 독립적인 트랜잭션 처리

## 🗂️ 프로젝트 구조

```
ex01/
├── user/              # User Service
│   ├── src/main/java/com/metacoding/user/
│   │   ├── users/    # User 도메인
│   │   └── core/     # 공통 유틸리티 (JWT, Filter 등)
│   └── build.gradle
├── product/           # Product Service
│   ├── src/main/java/com/metacoding/product/
│   │   ├── products/ # Product 도메인
│   │   └── core/     # 공통 유틸리티
│   └── build.gradle
├── order/             # Order Service
│   ├── src/main/java/com/metacoding/order/
│   │   ├── orders/   # Order 도메인
│   │   ├── clients/  # Feign Clients
│   │   └── core/     # 공통 유틸리티
│   └── build.gradle
├── delivery/          # Delivery Service
│   ├── src/main/java/com/metacoding/delivery/
│   │   ├── deliveries/ # Delivery 도메인
│   │   └── core/       # 공통 유틸리티
│   └── build.gradle
└── docker-compose.yml # 전체 서비스 오케스트레이션
```

## 🔧 설정 파일

각 서비스는 프로파일 기반 설정을 사용합니다:
- `application.properties`: 프로파일 설정 (`spring.profiles.active=dev`)
- `application-dev.properties`: 개발 환경 설정
- `application-prod.properties`: 프로덕션 환경 설정 (선택적)

## 📌 주의사항

1. **데이터베이스**: H2 인메모리 DB 사용으로 서비스 재시작 시 데이터 초기화
2. **네트워크**: Docker Compose의 `msa-network`를 통해 서비스 간 통신
3. **포트**: 각 서비스는 고정 포트 사용 (8081~8084)

## 🧪 테스트

각 서비스에는 기본 테스트가 포함되어 있습니다:

```bash
# 각 서비스 디렉토리에서
./gradlew test
```

## 📚 추가 정보

- 모든 서비스는 독립적으로 빌드 및 실행 가능
- Docker를 사용하여 컨테이너화된 환경에서 실행
- JWT 토큰은 User Service에서 발급되며, 다른 서비스에서 검증

