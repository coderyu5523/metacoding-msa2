# ex03 - Kafka 기반 Saga 패턴 구현

ex02를 기반으로 FeignClient 대신 Kafka를 사용한 비동기 Saga 패턴을 구현한 프로젝트입니다.

## 아키텍처 개요

### 이벤트 기반 통신
- **알림 이벤트 (Notification/Fact)**: 서비스가 자신의 작업을 완료했을 때 발행
  - `OrderCreated`: 주문이 생성됨
  - `ProductDecreased`: 상품이 차감됨
  - `DeliveryCreated`: 배달이 생성됨

- **명령 이벤트 (Command)**: 다른 서비스에게 작업을 요청할 때 발행
  - `DecreaseProductCommand`: 상품 차감 요청
  - `CreateDeliveryCommand`: 배달 생성 요청
  - `CompleteOrderCommand`: 주문 완료 요청

### Saga 오케스트레이션 흐름

```
1. Order Service
   - 주문 생성 (PENDING 상태)
   - OrderCreated 이벤트 발행
   - 즉시 응답 반환

2. Orchestrator Service (별도 서비스)
   - OrderCreated 이벤트 수신
   - DecreaseProductCommand 발행

3. Product Service
   - DecreaseProductCommand 수신
   - 상품 차감 처리 (원자적으로)
   - ProductDecreased 이벤트 발행 (성공/실패)

4. Orchestrator
   - ProductDecreased 이벤트 수신
   - 성공 시 CreateDeliveryCommand 발행

5. Delivery Service
   - CreateDeliveryCommand 수신
   - 배달 생성 처리
   - DeliveryCreated 이벤트 발행 (성공/실패)

6. Orchestrator
   - DeliveryCreated 이벤트 수신
   - 모든 단계 완료 시 CompleteOrderCommand 발행

7. Order Service
   - CompleteOrderCommand 수신
   - 주문 상태를 COMPLETED로 변경
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
- **Order Service**: `OrderCreated`, `CompleteOrderCommand`
- **Product Service**: `DecreaseProductCommand`, `ProductDecreased`
- **Delivery Service**: `CreateDeliveryCommand`, `DeliveryCreated`

### 4. Orchestrator Service
- 별도 서비스로 분리된 `Orchestrator` 구현
- 이벤트 수집 및 상태 관리
- 조건에 따라 명령 발행
- 독립적인 확장 및 배포 가능

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
# Orchestrator Service (먼저 실행)
cd orchestrator && ./gradlew bootRun

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
  "price": 10000
}
```

## Kafka 토픽

- `order-created`: 주문 생성 알림
- `decrease-product-command`: 상품 차감 명령
- `product-decreased`: 상품 차감 결과 알림
- `create-delivery-command`: 배달 생성 명령
- `delivery-created`: 배달 생성 결과 알림
- `complete-order-command`: 주문 완료 명령

## 특징

1. **비동기 처리**: 주문 생성 후 즉시 응답, 백그라운드에서 처리
2. **상태 관리**: Order는 PENDING 상태로 시작, 모든 단계 완료 후 COMPLETED로 변경
3. **원자적 처리**: 각 서비스는 자신의 DB만 원자적으로 처리
4. **이벤트 기반**: 서비스 간 느슨한 결합, 확장성 향상

## 주의사항

- Kafka 서버가 실행되어 있어야 합니다
- 실패 처리 및 보상 트랜잭션은 기본 구현만 포함되어 있습니다
- 프로덕션 환경에서는 더 강화된 에러 처리 및 재시도 로직이 필요합니다
