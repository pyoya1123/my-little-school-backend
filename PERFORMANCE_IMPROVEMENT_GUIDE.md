# 성능 개선 가이드라인 (Before/After 측정)

## 📋 목차
1. [개요](#개요)
2. [측정 환경 설정](#측정-환경-설정)
3. [Before 측정 (기준선 설정)](#before-측정-기준선-설정)
4. [성능 개선 포인트](#성능-개선-포인트)
5. [After 측정 (개선 후)](#after-측정-개선-후)
6. [결과 분석 및 서류 작성](#결과-분석-및-서류-작성)

---

## 개요

이 가이드는 **프로파일러와 k6 부하 테스트**를 활용하여 성능을 측정하고, **DB 최적화 기술**을 적용하여 개선하는 과정을 다룹니다.

### 목표
- **Before/After 비교**를 통한 정량적 성능 개선 증명
- **서류 작성용 데이터** 수집
- **실제 적용 가능한 최적화 기술** 학습

### 측정 도구
- **프로파일링**: Spring Boot Actuator, Hibernate Statistics
- **부하 테스트**: k6
- **DB 모니터링**: Hibernate Statistics, MySQL Slow Query Log

---

## 측정 환경 설정

### 1. 테스트 데이터 준비

```bash
# 사용자 1000명 생성
curl -X POST http://localhost:8080/test-data/users?count=1000

# 게시판 5000개 생성
curl -X POST http://localhost:8080/test-data/boards?count=5000&userId=1
```

### 2. 프로파일링 활성화 확인

`application.yml` 확인:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true  # 활성화 확인
```

### 3. MySQL Slow Query Log 활성화 (선택)

```sql
-- MySQL 설정 확인
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- 활성화 (필요시)
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 1초 이상 쿼리 기록
```

---

## Before 측정 (기준선 설정)

### 1. Hibernate 통계 리셋

```bash
# 통계 초기화
curl http://localhost:8080/profiling/hibernate-stats/reset
```

### 2. 부하 테스트 실행 (Before)

```bash
# k6 부하 테스트 실행
k6 run load-test-script.js --out json=before-results.json

# 또는 상세 로그와 함께
k6 run --out json=before-results.json --summary-export=before-summary.json load-test-script.js
```

### 3. Before 측정 지표 수집

#### 3.1 k6 결과 저장
```bash
# 결과를 파일로 저장
k6 run load-test-script.js --out json=before-results.json
```

**주요 지표**:
- `http_req_duration`: 평균 응답 시간
- `http_req_duration{p(95)}`: 95%ile 응답 시간
- `http_req_duration{p(99)}`: 99%ile 응답 시간
- `http_reqs`: 초당 요청 수 (RPS)
- `http_req_failed`: 실패율

#### 3.2 Hibernate 통계 수집
```bash
# 테스트 직후 통계 수집
curl http://localhost:8080/profiling/hibernate-stats > before-hibernate-stats.json
```

**주요 지표**:
- `queryExecutionCount`: 총 쿼리 실행 횟수
- `averageQueryExecutionTime`: 평균 쿼리 실행 시간
- `queryExecutionMaxTime`: 최대 쿼리 실행 시간
- `entityFetchCount`: 엔티티 페치 횟수 (N+1 감지)
- `collectionFetchCount`: 컬렉션 페치 횟수 (N+1 감지)

#### 3.3 Actuator 메트릭 수집
```bash
# HTTP 요청 시간
curl http://localhost:8080/actuator/metrics/http.request.duration > before-http-metrics.json

# 서비스 메서드 실행 시간
curl http://localhost:8080/actuator/metrics/service.method.duration > before-service-metrics.json

# Repository 메서드 실행 시간
curl http://localhost:8080/actuator/metrics/repository.method.duration > before-repository-metrics.json
```

### 4. Before 측정 결과 정리

**측정 결과 표 (예시)**:

| 지표 | 값 | 비고 |
|------|-----|------|
| 평균 응답 시간 | 450ms | k6 결과 |
| 95%ile 응답 시간 | 850ms | k6 결과 |
| 99%ile 응답 시간 | 1200ms | k6 결과 |
| RPS (초당 요청 수) | 25 req/s | k6 결과 |
| 실패율 | 0.5% | k6 결과 |
| 총 쿼리 실행 횟수 | 15,000 | Hibernate 통계 |
| 평균 쿼리 실행 시간 | 25ms | Hibernate 통계 |
| 엔티티 페치 횟수 | 8,000 | N+1 문제 가능성 |
| 컬렉션 페치 횟수 | 5,000 | N+1 문제 가능성 |

---

## 성능 개선 포인트

### 1. 데이터베이스 인덱스 추가

#### 1.1 인덱스가 필요한 컬럼 식별

**분석 방법**:
```sql
-- MySQL에서 실행 계획 확인
EXPLAIN SELECT * FROM user WHERE email = 'test@example.com';
EXPLAIN SELECT * FROM board WHERE user_id = 1;
EXPLAIN SELECT * FROM school WHERE school_name LIKE '%중학교%';
```

**인덱스 추가 대상** (예시):
- `user.email` - 사용자 이메일 조회
- `board.user_id` - 사용자별 게시판 조회
- `school.school_name` - 학교 이름 검색
- `friendship.requester_id`, `friendship.receiver_id` - 친구 관계 조회
- `comment.board_id` - 게시판별 댓글 조회

#### 1.2 인덱스 추가 (Before 측정 후)

```sql
-- 사용자 이메일 인덱스
CREATE INDEX idx_user_email ON user(user_email);

-- 게시판 사용자 ID 인덱스
CREATE INDEX idx_board_user_id ON board(user_id);

-- 학교 이름 인덱스 (LIKE 검색용)
CREATE INDEX idx_school_name ON school(school_name);

-- 친구 관계 복합 인덱스
CREATE INDEX idx_friendship_requester ON friendship(requester_id);
CREATE INDEX idx_friendship_receiver ON friendship(receiver_id);

-- 댓글 게시판 ID 인덱스
CREATE INDEX idx_comment_board_id ON comment(board_id);
```

#### 1.3 JPA에서 인덱스 정의 (선택)

```java
@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_user_email", columnList = "user_email"),
    @Index(name = "idx_user_school_id", columnList = "school_id")
})
public class User {
    // ...
}
```

**측정 방법**:
- Before: 인덱스 없이 쿼리 실행 시간 측정
- After: 인덱스 추가 후 동일 쿼리 실행 시간 측정
- 개선율: `(Before 시간 - After 시간) / Before 시간 * 100`

---

### 2. N+1 쿼리 문제 해결

#### 2.1 N+1 문제 감지

**현재 문제 코드 예시**:
```java
// BoardService.java
public List<BoardListResponseDTO> getAllBoards(Integer userId) {
    return boardRepository.findAll()  // 1번 쿼리
        .stream().map(b -> new BoardListResponseDTO(
                b,
                commentService.getCommentCountByBoardId(b.getId()),  // N번 쿼리
                boardLikeService.isExistLike(...)  // N번 쿼리
            )
        )
        .toList();
}
```

**문제**: 게시판 100개 조회 시 → 1 + 100 + 100 = 201개 쿼리

#### 2.2 JOIN FETCH로 해결

```java
// BoardRepository.java
@Query("SELECT b FROM Board b " +
       "LEFT JOIN FETCH b.comments " +
       "LEFT JOIN FETCH b.likes " +
       "WHERE b.userId = :userId")
List<Board> findAllWithCommentsAndLikes(@Param("userId") Integer userId);
```

또는 **@EntityGraph** 사용:
```java
@EntityGraph(attributePaths = {"comments", "likes"})
List<Board> findAll();
```

#### 2.3 배치 페치 크기 조정

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

**측정 방법**:
- Before: `entityFetchCount`, `collectionFetchCount` 확인
- After: JOIN FETCH 적용 후 동일 지표 확인
- 개선율: 쿼리 실행 횟수 감소율

---

### 3. 페이징 적용

#### 3.1 페이징이 필요한 메서드 식별

**현재 문제 코드**:
```java
// UserService.java
public List<UserDTO> getAllUser() {
    return userRepository.findAll()  // 전체 조회
        .stream()
        .map(UserDTO::new)
        .toList();
}
```

#### 3.2 Pageable 적용

```java
// UserService.java
public Page<UserDTO> getAllUser(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(UserDTO::new);
}

// UserController.java
@GetMapping("/list")
public ResponseResult<Page<UserDTO>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    return success(userService.getAllUser(pageable));
}
```

**측정 방법**:
- Before: 전체 데이터 조회 시간 측정
- After: 페이징 적용 후 첫 페이지 조회 시간 측정
- 개선율: 응답 시간 감소율

---

### 4. 낙관적 락 (Optimistic Lock) 적용

#### 4.1 낙관적 락이 필요한 엔티티

**적용 대상**:
- 동시 수정이 빈번한 엔티티
- 예: `User` (레벨, 골드 업데이트), `Board` (좋아요 수)

#### 4.2 @Version 필드 추가

```java
@Entity
public class User {
    // ...
    
    @Version
    @Column(name = "version")
    private Long version;  // 낙관적 락용 버전 필드
}
```

#### 4.3 낙관적 락 예외 처리

```java
@Transactional
public UserDTO updateUser(UserUpdateDTO dto) {
    try {
        User user = userRepository.findById(dto.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        // 수정 로직
        user.setName(dto.getName());
        // ...
        
        return new UserDTO(userRepository.save(user));
    } catch (OptimisticLockingFailureException e) {
        // 동시 수정 충돌 처리
        throw new ConflictException("다른 사용자가 수정 중입니다. 다시 시도해주세요.");
    }
}
```

**측정 방법**:
- Before: 동시 수정 시 충돌 발생 빈도 측정
- After: 낙관적 락 적용 후 충돌 처리 시간 측정
- 개선율: 충돌 처리 시간 단축

---

### 5. 배치 업데이트 최적화

#### 5.1 개별 업데이트 → 배치 업데이트

**현재 문제 코드**:
```java
// UserService.java
@Transactional
public void setAllUserStatusToOffline() {
    List<User> userList = userRepository.findAll();
    userList.forEach(user -> {
        user.setIsOnline(false);
        userRepository.save(user);  // 각각 save
    });
}
```

**개선 코드**:
```java
// UserRepository.java
@Modifying
@Query("UPDATE User u SET u.isOnline = false")
void setAllUserStatusToOffline();
```

**측정 방법**:
- Before: 개별 업데이트 시간 측정
- After: 배치 업데이트 시간 측정
- 개선율: 업데이트 시간 감소율

---

### 6. 쿼리 최적화

#### 6.1 불필요한 조인 제거

**분석**:
```sql
-- 실행 계획 확인
EXPLAIN SELECT u.*, s.* 
FROM user u 
LEFT JOIN school s ON u.school_id = s.id 
WHERE u.id = 1;
```

#### 6.2 필요한 컬럼만 조회

```java
// 전체 엔티티 조회 대신 필요한 필드만
@Query("SELECT new com.project.final_project.user.dto.UserProfileDTO(" +
       "u.id, u.name, u.interest, u.statusMessage) " +
       "FROM User u WHERE u.id = :userId")
UserProfileDTO getProfileById(@Param("userId") Integer userId);
```

---

### 7. Redis 캐싱 적용

#### 7.1 캐싱이 필요한 데이터

- 자주 조회되지만 변경이 적은 데이터
- 예: 학교 목록, 아이템 목록, 퀘스트 목록

#### 7.2 @Cacheable 적용

```java
// SchoolService.java
@Cacheable(value = "schools", key = "#schoolId")
public SchoolDTO getSchoolById(Integer schoolId) {
    return schoolRepository.findById(schoolId)
        .map(SchoolDTO::new)
        .orElseThrow(() -> new NotFoundException("School not found"));
}

@CacheEvict(value = "schools", key = "#schoolId")
public void updateSchool(Integer schoolId, SchoolUpdateDTO dto) {
    // 업데이트 로직
}
```

**측정 방법**:
- Before: DB 직접 조회 시간 측정
- After: 캐시 히트 시 조회 시간 측정
- 개선율: 캐시 히트율 및 응답 시간 감소율

---

## After 측정 (개선 후)

### 1. 개선 사항 적용

각 개선 포인트를 **하나씩** 적용하고 측정하는 것을 권장합니다.

### 2. 통계 리셋

```bash
# 통계 초기화
curl http://localhost:8080/profiling/hibernate-stats/reset
```

### 3. 부하 테스트 재실행

```bash
# After 측정
k6 run load-test-script.js --out json=after-results.json
```

### 4. After 측정 지표 수집

```bash
# Hibernate 통계
curl http://localhost:8080/profiling/hibernate-stats > after-hibernate-stats.json

# Actuator 메트릭
curl http://localhost:8080/actuator/metrics/http.request.duration > after-http-metrics.json
```

### 5. Before/After 비교

**비교 표 (예시)**:

| 지표 | Before | After | 개선율 |
|------|--------|-------|--------|
| 평균 응답 시간 | 450ms | 280ms | **37.8% 개선** |
| 95%ile 응답 시간 | 850ms | 450ms | **47.1% 개선** |
| 99%ile 응답 시간 | 1200ms | 650ms | **45.8% 개선** |
| RPS | 25 req/s | 42 req/s | **68% 증가** |
| 실패율 | 0.5% | 0.1% | **80% 감소** |
| 총 쿼리 실행 횟수 | 15,000 | 8,000 | **46.7% 감소** |
| 평균 쿼리 실행 시간 | 25ms | 12ms | **52% 개선** |
| 엔티티 페치 횟수 | 8,000 | 2,000 | **75% 감소** |

---

## 결과 분석 및 서류 작성

### 1. 측정 결과 정리

#### 1.1 개선 항목별 결과

**예시: 인덱스 추가**

| 항목 | Before | After | 개선율 |
|------|--------|-------|--------|
| 사용자 이메일 조회 | 120ms | 15ms | **87.5% 개선** |
| 게시판 목록 조회 | 450ms | 180ms | **60% 개선** |
| 학교 검색 | 320ms | 45ms | **85.9% 개선** |

#### 1.2 쿼리 실행 계획 비교

**Before**:
```
EXPLAIN 결과: type=ALL, rows=10000 (전체 스캔)
```

**After**:
```
EXPLAIN 결과: type=ref, rows=1 (인덱스 사용)
```

### 2. 서류 작성 포인트

#### 2.1 문제 인식

```
"초기 성능 측정 결과, 게시판 목록 조회 API의 평균 응답 시간이 450ms로 
사용자 경험에 부정적 영향을 미치는 것으로 확인되었습니다. 
Hibernate 통계 분석 결과, N+1 쿼리 문제로 인해 게시판 100개 조회 시 
201개의 쿼리가 실행되는 것을 발견했습니다."
```

#### 2.2 개선 방안

```
"다음과 같은 최적화 기법을 적용했습니다:

1. 데이터베이스 인덱스 추가
   - user.email, board.user_id, school.school_name 등 
     자주 조회되는 컬럼에 인덱스 추가
   - 쿼리 실행 계획 분석을 통한 최적 인덱스 설계

2. N+1 쿼리 문제 해결
   - JOIN FETCH를 활용한 연관 엔티티 한 번에 조회
   - @EntityGraph를 통한 페치 전략 최적화

3. 페이징 적용
   - Pageable을 활용한 대량 데이터 조회 최적화
   - 불필요한 데이터 로딩 방지

4. 낙관적 락 적용
   - @Version 필드를 통한 동시성 제어
   - OptimisticLockingFailureException 처리
```

#### 2.3 개선 결과

```
"최적화 적용 후 측정 결과:

- 평균 응답 시간: 450ms → 280ms (37.8% 개선)
- 95%ile 응답 시간: 850ms → 450ms (47.1% 개선)
- 쿼리 실행 횟수: 15,000회 → 8,000회 (46.7% 감소)
- 초당 처리 가능한 요청 수: 25 req/s → 42 req/s (68% 증가)

이를 통해 사용자 경험을 크게 개선하고, 서버 리소스 사용량을 
효율적으로 관리할 수 있게 되었습니다."
```

### 3. 시각화 자료

#### 3.1 그래프 제안

- **응답 시간 비교 그래프** (Before/After)
- **쿼리 실행 횟수 비교 그래프**
- **RPS 비교 그래프**
- **개선 항목별 기여도 파이 차트**

#### 3.2 코드 스니펫

서류에 포함할 코드 예시:
- Before 코드 (문제점)
- After 코드 (개선 코드)
- 실행 계획 비교 (EXPLAIN 결과)

---

## 단계별 실행 가이드

### Step 1: Before 측정 (1일)

1. 테스트 데이터 생성 (1000명 사용자, 5000개 게시판)
2. 통계 리셋
3. k6 부하 테스트 실행
4. 모든 지표 수집 및 저장
5. 결과 정리

### Step 2: 개선 사항 적용 (2-3일)

**우선순위별 적용**:

1. **인덱스 추가** (가장 빠른 효과)
   - 실행 계획 분석
   - 인덱스 추가
   - 재측정

2. **N+1 쿼리 해결**
   - 문제 코드 식별
   - JOIN FETCH 적용
   - 재측정

3. **페이징 적용**
   - findAll → Pageable 변경
   - 재측정

4. **낙관적 락 적용** (선택)
   - @Version 필드 추가
   - 예외 처리
   - 재측정

5. **캐싱 적용** (선택)
   - @Cacheable 적용
   - 재측정

### Step 3: After 측정 (1일)

1. 모든 개선 사항 적용 후
2. 통계 리셋
3. k6 부하 테스트 재실행
4. 모든 지표 수집
5. Before/After 비교

### Step 4: 결과 정리 및 서류 작성 (1일)

1. 측정 결과 표 정리
2. 개선율 계산
3. 서류 작성
4. 시각화 자료 생성

---

## 주의사항

### 1. 측정 환경 일관성

- **동일한 테스트 데이터** 사용
- **동일한 부하 테스트 시나리오** 사용
- **동일한 서버 환경** 사용

### 2. 개선 사항별 측정

- **하나씩 적용하고 측정** (원인 파악 용이)
- 각 개선 사항의 기여도를 명확히 파악

### 3. 프로덕션 고려

- 인덱스 추가 시 **디스크 공간** 고려
- 낙관적 락 적용 시 **충돌 처리 로직** 필수
- 캐싱 적용 시 **캐시 무효화 전략** 필요

---

## 참고 자료

- [Hibernate Performance Tuning](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#performance)
- [MySQL Index Optimization](https://dev.mysql.com/doc/refman/8.0/en/optimization-indexes.html)
- [Spring Data JPA Best Practices](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [k6 Documentation](https://k6.io/docs/)

---

## 체크리스트

### Before 측정
- [ ] 테스트 데이터 생성 완료
- [ ] 통계 리셋 완료
- [ ] k6 부하 테스트 실행
- [ ] Hibernate 통계 수집
- [ ] Actuator 메트릭 수집
- [ ] 결과 파일 저장

### 개선 적용
- [ ] 인덱스 추가
- [ ] N+1 쿼리 해결
- [ ] 페이징 적용
- [ ] 낙관적 락 적용 (선택)
- [ ] 캐싱 적용 (선택)

### After 측정
- [ ] 통계 리셋 완료
- [ ] k6 부하 테스트 재실행
- [ ] 모든 지표 수집
- [ ] Before/After 비교

### 서류 작성
- [ ] 측정 결과 표 정리
- [ ] 개선율 계산
- [ ] 문제 인식 및 개선 방안 작성
- [ ] 시각화 자료 생성

