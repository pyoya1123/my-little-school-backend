# 성능 테스트 가이드

## 📋 목차
1. [개요](#개요)
2. [목업 데이터 생성](#목업-데이터-생성)
3. [k6 부하 테스트 실행](#k6-부하-테스트-실행)
4. [테스트 결과 분석](#테스트-결과-분석)
5. [성능 최적화 체크리스트](#성능-최적화-체크리스트)

---

## 개요

이 프로젝트는 **k6**를 사용한 부하 테스트와 **목업 데이터 생성** 기능을 제공합니다.

### 준비 사항
- k6 설치: https://k6.io/docs/getting-started/installation/
- 애플리케이션 실행 중
- 데이터베이스 연결 확인

---

## 목업 데이터 생성

### 1. API를 통한 데이터 생성

#### 사용자 데이터 생성
```bash
# 기본: 10명의 사용자 생성
curl -X POST http://localhost:8080/test-data/users?count=10

# 특정 학교에 50명의 사용자 생성
curl -X POST http://localhost:8080/test-data/users?count=50&schoolId=1
```

**응답 예시**:
```json
{
  "createdCount": 10,
  "requestedCount": 10,
  "userIds": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
}
```

#### 게시판 데이터 생성
```bash
# 기본: 10개의 게시판 생성 (랜덤 사용자)
curl -X POST http://localhost:8080/test-data/boards?count=10

# 특정 사용자에게 50개의 게시판 생성
curl -X POST http://localhost:8080/test-data/boards?count=50&userId=1
```

**응답 예시**:
```json
{
  "createdCount": 50,
  "requestedCount": 50,
  "boardIds": [1, 2, 3, ...],
  "userId": 1
}
```

### 2. MockDataGenerator 사용

Java 코드에서 직접 사용할 수 있습니다:

```java
import com.project.final_project.common.util.MockDataGenerator;

// 사용자 데이터 생성
MockDataGenerator.UserRegisterData userData = 
    MockDataGenerator.generateUserRegisterData(schoolId);

// 게시판 데이터 생성
MockDataGenerator.BoardRegisterData boardData = 
    MockDataGenerator.generateBoardRegisterData(userId);
```

### 3. 생성되는 데이터 특징

- **사용자 데이터**:
  - 한국어 이름 (Faker 사용)
  - 1~3학년 랜덤
  - 2008~2010년생 랜덤
  - 랜덤 이메일
  - 1~3개의 관심사
  - 현실적인 전화번호

- **게시판 데이터**:
  - 한국어 제목 (3~8단어)
  - 한국어 본문 (3~5문단)

---

## k6 부하 테스트 실행

### 1. 기본 실행

```bash
k6 run load-test-script.js
```

### 2. 환경 변수 설정

```bash
# 서버 URL 변경
k6 run --env BASE_URL=http://localhost:8080 load-test-script.js
```

### 3. 테스트 시나리오 설명

현재 설정된 시나리오:

```
1. Warm-up (30초): 0 → 50명
2. Load (2분): 50 → 100명 유지
3. Stress (1분): 100 → 200명
4. Recovery (1분): 200 → 100명
5. Cool-down (30초): 100 → 0명
```

### 4. 임계치 (Thresholds)

- **http_req_duration**: 
  - 95%의 요청이 500ms 이내
  - 99%의 요청이 1000ms 이내
- **http_req_failed**: 1% 미만의 실패율
- **errors**: 10% 미만의 에러율

### 5. 테스트 시나리오 커스터마이징

`load-test-script.js`의 `options` 섹션을 수정:

```javascript
export const options = {
  stages: [
    { duration: '1m', target: 100 },  // 1분 동안 100명
    { duration: '3m', target: 100 },  // 3분 동안 100명 유지
    { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], // 더 엄격한 기준
  },
};
```

---

## 테스트 결과 분석

### 1. k6 출력 결과 해석

k6 실행 후 다음과 같은 결과가 출력됩니다:

```
✓ 게시판 목록 조회 성공: 100%
✓ 응답 시간 < 500ms: 95%
✓ 게시판 상세 조회 성공: 100%

checks.........................: 100% ✓ 1500   ✗ 0
data_received..................: 2.5 MB  42 kB/s
data_sent......................: 450 kB  7.5 kB/s
http_req_duration..............: avg=234ms  min=89ms  med=198ms  max=1200ms  p(90)=456ms  p(95)=512ms
http_req_failed................: 0.00%  ✓ 0      ✗ 1500
http_reqs......................: 1500   25.0/s
iteration_duration.............: avg=2.5s   min=1.2s   med=2.3s   max=5.1s
iterations.....................: 500    8.33/s
vus............................: 1      min=1      max=200
vus_max........................: 200    min=200    max=200
```

**주요 지표**:
- `http_req_duration`: 평균 응답 시간
- `p(95)`: 95%의 요청이 이 시간 이내에 완료
- `http_req_failed`: 실패율
- `http_reqs`: 초당 요청 수 (RPS)

### 2. 프로파일링 결과 확인

부하 테스트 중 또는 이후에 다음 엔드포인트로 성능 데이터 확인:

```bash
# Hibernate 통계
curl http://localhost:8080/profiling/hibernate-stats

# Actuator 메트릭
curl http://localhost:8080/actuator/metrics/http.request.duration
curl http://localhost:8080/actuator/prometheus
```

### 3. 병목 지점 파악

#### HTTP 요청 시간 분석
```bash
# 특정 엔드포인트의 응답 시간 확인
curl http://localhost:8080/actuator/metrics/http.request.duration | jq
```

#### 데이터베이스 쿼리 분석
```bash
# Hibernate 통계 확인
curl http://localhost:8080/profiling/hibernate-stats | jq
```

**확인 사항**:
- `queryExecutionCount`: 총 쿼리 실행 횟수
- `averageQueryExecutionTime`: 평균 쿼리 실행 시간
- `entityFetchCount`: 엔티티 페치 횟수 (N+1 문제 감지)

#### 서비스/Repository 메서드 분석
```bash
# 서비스 메서드 실행 시간
curl http://localhost:8080/actuator/metrics/service.method.duration | jq

# Repository 메서드 실행 시간
curl http://localhost:8080/actuator/metrics/repository.method.duration | jq
```

---

## 성능 최적화 체크리스트

### 테스트 전 체크리스트
- [ ] 목업 데이터 생성 완료
- [ ] 데이터베이스 인덱스 확인
- [ ] 애플리케이션 로그 레벨 설정 (INFO 이상)
- [ ] 프로파일링 활성화 확인

### 테스트 중 모니터링
- [ ] CPU 사용률
- [ ] 메모리 사용률
- [ ] 데이터베이스 연결 풀 상태
- [ ] 느린 쿼리 로그 확인

### 테스트 후 분석
- [ ] 평균 응답 시간 확인
- [ ] 95%ile, 99%ile 응답 시간 확인
- [ ] 에러율 확인
- [ ] N+1 쿼리 문제 확인
- [ ] 느린 엔드포인트 식별

### 최적화 포인트
- [ ] 페이징 적용 (findAll → Pageable)
- [ ] JOIN FETCH로 N+1 문제 해결
- [ ] Redis 캐싱 적용
- [ ] 불필요한 쿼리 제거
- [ ] 인덱스 추가

---

## 예시: 전체 테스트 플로우

### 1. 테스트 데이터 생성
```bash
# 사용자 100명 생성
curl -X POST http://localhost:8080/test-data/users?count=100

# 게시판 500개 생성
curl -X POST http://localhost:8080/test-data/boards?count=500
```

### 2. 부하 테스트 실행
```bash
k6 run load-test-script.js
```

### 3. 결과 분석
```bash
# Hibernate 통계 확인
curl http://localhost:8080/profiling/hibernate-stats | jq

# 메트릭 확인
curl http://localhost:8080/actuator/prometheus | grep http_request_duration
```

### 4. 최적화 적용
- 발견된 문제점에 따라 코드 최적화
- 다시 테스트하여 개선 확인

---

## 주의사항

1. **프로덕션 환경**: 
   - `/test-data` 엔드포인트는 프로덕션에서 비활성화 필요
   - 보안 설정 추가 권장

2. **데이터베이스**:
   - 대량 데이터 생성 시 트랜잭션 타임아웃 주의
   - 인덱스가 없는 상태에서 테스트 시 성능 저하 가능

3. **리소스**:
   - 부하 테스트는 서버 리소스를 많이 사용
   - 테스트 환경에서만 실행

---

## 참고 자료

- [k6 공식 문서](https://k6.io/docs/)
- [k6 JavaScript API](https://k6.io/docs/javascript-api/)
- [Faker 라이브러리](https://github.com/DiUS/java-faker)

