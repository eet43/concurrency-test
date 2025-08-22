# 🎫 Spring Boot 동시성 제어 시스템

> Redis 분산락과 JPA 비관적 락을 활용한 이벤트 티켓 예약 시스템

## 📋 프로젝트 소개

실무에서 자주 발생하는 동시성 이슈(Lost Update)를 해결하기 위한 완전한 예제 프로젝트입니다.<br>
이벤트 티켓 예약 시나리오를 통해 세 가지 방식의 동시성 제어를 비교 분석합니다.

### 🎯 주요 기능
- **Redis 분산락**: 마이크로서비스 환경에서의 동시성 제어
- **JPA 비관적 락**: 데이터베이스 레벨에서의 동시성 제어  
- **락 없는 버전**: Lost Update 문제 재현

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.2.0, Spring Data JPA
- **Database**: H2 Database (개발용)
- **Cache**: Redis 7, Redisson
- **Container**: Docker, Docker Compose
- **Build**: Maven

## 🚀 빠른 시작

### 1. 환경 구성
```bash
# 저장소 클론
git clone [repository-url]
cd concurrency-test

# Redis + H2 컨테이너 실행
docker-compose up -d
```

### 2. 애플리케이션 실행
```bash
# IDE에서 ConcurrencyTestApplication.main() 실행
# 또는
mvn spring-boot:run
```

## 📊 동시성 테스트

### 분산락 테스트
```bash
# 15개 동시 요청
for i in {1..15}; do 
  curl -X POST http://localhost:8080/api/events/reserve/1/distributed-lock & 
done; wait
```

### 비관적 락 테스트
```bash
# 15개 동시 요청
for i in {1..15}; do 
  curl -X POST http://localhost:8080/api/events/reserve/2/pessimistic-lock & 
done; wait
```

### 락 없음 테스트 (Lost Update 확인)
```bash
# 15개 동시 요청
for i in {1..15}; do 
  curl -X POST http://localhost:8080/api/events/reserve/3/no-lock & 
done; wait
```

## 📈 테스트 결과 비교

| 방식 | 요청 수 | 예상 차감 | 실제 차감 | Lost Update | 동시성 제어 |
|------|---------|----------|----------|-------------|-------------|
| **분산락** | 15개 | 15장 | ✅ 15장 | 0개 | ✅ 완벽 |
| **비관적 락** | 15개 | 15장 | ✅ 15장 | 0개 | ✅ 완벽 |
| **락 없음** | 15개 | 15장 | ❌ 1장 | 14개 | ❌ 실패 |


## 📁 프로젝트 구조

```
src/main/java/com/example/concurrency/
├── annotation/
│   └── DistributedLock.java          # 분산락 어노테이션
├── aspect/
│   └── DistributedLockAspect.java    # 분산락 AOP 구현
├── config/
│   └── RedisConfig.java              # Redis 설정
├── controller/
│   ├── EventController.java          # 이벤트 조회 API
│   └── ReserveController.java        # 예약 API (3가지 방식)
├── entity/
│   └── Event.java                    # 이벤트 엔티티
├── repository/
│   └── EventRepository.java          # JPA Repository (비관적 락 쿼리 포함)
└── service/
    ├── EventService.java             # 이벤트 서비스
    └── ReserveService.java           # 예약 서비스 (3가지 방식)
```

## 🔍 학습 포인트

### 1. 분산락 vs 비관적 락
- **분산락**: 애플리케이션 레벨, 다중 인스턴스 환경
- **비관적 락**: 데이터베이스 레벨, 단일 DB 환경

### 2. 트랜잭션 범위의 중요성
- **분산락**: `saveAndFlush()` 필수 (락 범위 안에서 DB 커밋 완료)
- **비관적 락**: `save()` 충분 (트랜잭션이 전체 보호)

### 3. Lost Update 패턴
```
여러 요청이 동일한 초기값 읽기 → 동일한 결과값으로 덮어쓰기 → 마지막 저장만 유효
```

## 🎓 실무 적용 가이드

### 분산락 사용 시기
- 마이크로서비스 환경
- 여러 인스턴스에서 동일 자원 접근
- Redis 인프라 보유

### 비관적 락 사용 시기  
- 단일 데이터베이스 환경
- 높은 경합 상황
- 빠른 응답 속도 필요



**💡 Tip**: 실제 운영 환경에서는 락 타임아웃, 재시도 전략, 모니터링 등을 추가로 고려해야 합니다.
