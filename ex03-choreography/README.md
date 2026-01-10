# ex03-choreography - Kafka 기반 Saga 패턴 구현 (Choreography)

ex02를 기반으로 FeignClient 대신 Kafka를 사용한 비동기 Saga 패턴을 **Choreography 방식**으로 구현한 프로젝트입니다.

## 아키텍처 개요

### Choreography 패턴
- 중앙 오케스트레이터 없이 각 서비스가 이벤트를 직접 구독하고 처리
- 서비스 간 느슨한 결합
- 각 서비스가 자신의 책임에 맞는 이벤트를 구독하고 다음 단계를 진행

### 이벤트 기반 통신
- **알림 이벤트 (Notification/Fact)**: 서비스가 자신의 작업을 완료했을 때 발행
  - `OrderCreated`: 주문이 생성됨
  - `ProductDecreased`: 상품이 차감됨
  - `DeliveryCreated`: 배달이 생성됨

- **명령 이벤트 (Command)**: 보상 트랜잭션용
  - `IncreaseProductCommand`: 상품 재고 복구 요청

### Saga Choreography 흐름

```
1. Order Service
   - 주문 생성 (PENDING 상태)
   - OrderCreated 이벤트 발행
   - 즉시 응답 반환

2. Product Service
   - OrderCreated 이벤트 직접 구독
   - 상품 차감 처리 (원자적으로)
   - ProductDecreased 이벤트 발행 (성공/실패)

3. Delivery Service
   - OrderCreated 이벤트 구독 (주소 정보 저장)
   - ProductDecreased 이벤트 직접 구독
   - 성공 시 배달 생성 처리
   - DeliveryCreated 이벤트 발행 (성공/실패)
   - 실패 시 IncreaseProductCommand 발행 (보상 트랜잭션)

4. Order Service
   - DeliveryCreated 이벤트 직접 구독
   - 성공 시 주문 상태를 COMPLETED로 변경
   - 실패 시 주문 상태를 CANCELLED로 변경
```

## 주요 변경사항

### 1. FeignClient 제거
- `ProductClient`, `DeliveryClient` 제거
- `@EnableFeignClients` 제거
- `spring-cloud-starter-openfeign` 의존성 제거

### 2. Kafka 의존성 추가
- 모든 서비스에 `spring-kafka` 의존성 추가
- Kafka Producer/Consumer 구현

### 3. 이벤트 모델
- **Order Service**: `OrderCreated` 발행, `DeliveryCreated` 구독
- **Product Service**: `OrderCreated` 구독, `ProductDecreased` 발행, `IncreaseProductCommand` 구독 (보상)
- **Delivery Service**: `OrderCreated` 구독 (주소 저장), `ProductDecreased` 구독, `DeliveryCreated` 발행, `IncreaseProductCommand` 발행 (보상)

### 4. Orchestrator 서비스 제거
- 중앙 오케스트레이터 없이 각 서비스가 직접 이벤트를 구독하고 처리
- 서비스 간 직접 통신 없이 이벤트 기반으로 협력

## 실행 방법

### 1. Kafka 실행 (Minikube)
```bash
# Minikube에서 Kafka 배포 (KRaft 모드 - Zookeeper 없이 실행)
kubectl apply -f k8s/kafka/

# 배포 상태 확인
kubectl get pods -n metacoding -l app=kafka

# 로그 확인
kubectl logs -n metacoding -l app=kafka
```

### 2. 애플리케이션 실행
```bash
# Order Service
cd order && ./gradlew bootRun

# Product Service
cd product && ./gradlew bootRun

# Delivery Service
cd delivery && ./gradlew bootRun
```

### 3. 주문 생성 요청
```bash
POST http://localhost:8081/orders
{
  "userId": 1,
  "productId": 1,
  "quantity": 1,
  "price": 10000,
  "address": "서울시 강남구"
}
```

## Kafka 토픽

- `order-created`: 주문 생성 알림
- `product-decreased`: 상품 차감 결과 알림
- `delivery-created`: 배달 생성 결과 알림
- `increase-product-command`: 상품 재고 복구 명령 (보상 트랜잭션)

## 특징

1. **비동기 처리**: 주문 생성 후 즉시 응답, 백그라운드에서 처리
2. **상태 관리**: Order는 PENDING 상태로 시작, 모든 단계 완료 후 COMPLETED로 변경
3. **원자적 처리**: 각 서비스는 자신의 DB만 원자적으로 처리
4. **이벤트 기반**: 서비스 간 느슨한 결합, 확장성 향상
5. **Choreography 패턴**: 중앙 오케스트레이터 없이 각 서비스가 자율적으로 협력

## Choreography vs Orchestration

### Choreography (현재 구현)
- ✅ 중앙 집중식 컴포넌트 없음
- ✅ 서비스 간 느슨한 결합
- ✅ 단일 장애점 없음
- ❌ 전체 플로우 추적이 어려움
- ❌ 복잡한 비즈니스 로직 관리가 어려움

### Orchestration (ex03-orchestrator)
- ✅ 전체 플로우 가시성과 추적 용이
- ✅ 복잡한 비즈니스 로직 관리 용이
- ✅ 트랜잭션 상태 관리 명확
- ❌ 오케스트레이터가 단일 장애점이 될 수 있음
- ❌ 추가 컴포넌트 필요

## 주의사항

- Kafka 서버가 실행되어 있어야 합니다
- 실패 처리 및 보상 트랜잭션은 기본 구현만 포함되어 있습니다
- 프로덕션 환경에서는 더 강화된 에러 처리 및 재시도 로직이 필요합니다
- Delivery 서비스는 OrderCreated 이벤트를 구독하여 주소 정보를 임시 저장합니다