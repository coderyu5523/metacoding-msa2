# Docker Compose를 사용한 로컬 테스트

이 문서는 Docker Compose를 사용하여 전체 마이크로서비스 스택을 로컬에서 실행하는 방법을 설명합니다.

## 사전 요구사항

- Docker
- Docker Compose

## 서비스 구성

다음 서비스들이 포함됩니다:

1. **Kafka** (KRaft 모드) - 포트 9092
2. **User Service** - 포트 8083
3. **Product Service** - 포트 8082
4. **Order Service** - 포트 8081
5. **Delivery Service** - 포트 8084
6. **Orchestrator Service** - 포트 8085
7. **API Gateway** - 포트 8080

## 실행 방법

### 1. 전체 스택 실행

```bash
docker-compose up --build
```

### 2. 백그라운드 실행

```bash
docker-compose up -d --build
```

### 3. 특정 서비스만 실행

```bash
# Kafka만 실행
docker-compose up kafka

# 특정 서비스들만 실행
docker-compose up kafka order-service product-service
```

### 4. 로그 확인

```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f order-service
docker-compose logs -f kafka
```

### 5. 서비스 중지

```bash
# 서비스 중지 (컨테이너 유지)
docker-compose stop

# 서비스 중지 및 컨테이너 제거
docker-compose down

# 볼륨까지 제거
docker-compose down -v
```

## 서비스 접근

- **API Gateway**: http://localhost:8080
- **Order Service**: http://localhost:8081
- **Product Service**: http://localhost:8082
- **User Service**: http://localhost:8083
- **Delivery Service**: http://localhost:8084
- **Orchestrator Service**: http://localhost:8085
- **Kafka**: localhost:9092

## 환경 변수

각 서비스는 다음 환경 변수를 통해 설정됩니다:

- `SPRING_PROFILES_ACTIVE=dev`: 개발 프로파일 사용
- `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`: Kafka 브로커 주소

## 문제 해결

### Kafka가 시작되지 않는 경우

```bash
# Kafka 로그 확인
docker-compose logs kafka

# Kafka 컨테이너 재시작
docker-compose restart kafka
```

### 서비스가 Kafka에 연결되지 않는 경우

1. Kafka가 완전히 시작되었는지 확인 (healthcheck 통과)
2. 네트워크 연결 확인: `docker network inspect ex03_msa-network`
3. 서비스 로그 확인: `docker-compose logs <service-name>`

### 빌드 오류가 발생하는 경우

```bash
# 캐시 없이 재빌드
docker-compose build --no-cache

# 특정 서비스만 재빌드
docker-compose build --no-cache order-service
```

## 데이터 영속성

Kafka 데이터는 `kafka-data` 볼륨에 저장됩니다. 완전히 초기화하려면:

```bash
docker-compose down -v
```

## 개발 팁

1. **코드 변경 후 재빌드**: 서비스 코드를 변경한 경우 `docker-compose up --build`로 재빌드
2. **특정 서비스만 재시작**: `docker-compose restart order-service`
3. **컨테이너 내부 접근**: `docker-compose exec order-service sh`









