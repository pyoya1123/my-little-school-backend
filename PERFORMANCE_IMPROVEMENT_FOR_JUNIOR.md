# 신입 개발자용 성능 개선 가이드라인

## 🎯 목표
- **서류/면접에 활용 가능한** 성능 개선 경험
- **Before/After 비교**를 통한 정량적 증명
- **실제 적용 가능한 기술** 학습

---

## 📊 추천 기술 스택 (우선순위별)

### 1순위: 데이터베이스 인덱스 ⭐⭐⭐
**이유**: 
- 가장 기본적이고 효과가 명확함
- 면접에서 설명하기 쉬움
- Before/After 차이가 극명하게 드러남

**예상 개선율**: 50~90% (쿼리 타입에 따라)

### 2순위: N+1 쿼리 문제 해결 ⭐⭐⭐
**이유**:
- JPA에서 가장 흔한 문제
- JOIN FETCH로 해결하는 과정이 학습 가치 높음
- 쿼리 수 감소가 명확하게 측정 가능

**예상 개선율**: 40~70% (쿼리 수 감소)

### 3순위: 페이징 적용 ⭐⭐
**이유**:
- 간단하고 효과적
- 실제 서비스에서 필수적
- 메모리 사용량 감소도 측정 가능

**예상 개선율**: 30~60% (대량 데이터 조회 시)

### 4순위: 쿼리 최적화 (선택) ⭐
**이유**:
- 실행 계획 분석 경험
- 불필요한 조인 제거 등

**예상 개선율**: 20~40%

---

## 🚀 단계별 실행 가이드

### Phase 1: Before 측정 (30분)

#### 1.1 테스트 데이터 생성

```bash
# 사용자 500명 생성
curl -X POST http://localhost:8080/test-data/users?count=500

# 게시판 2000개 생성
curl -X POST http://localhost:8080/test-data/boards?count=2000&userId=1
```

#### 1.2 통계 초기화

```bash
# Hibernate 통계 리셋
curl http://localhost:8080/profiling/hibernate-stats/reset
```

#### 1.3 부하 테스트 실행 (Before)

```bash
# k6 부하 테스트 실행 (결과를 파일로 저장)
k6 run load-test-script.js --out json=before-results.json
```

**결과 확인 포인트**:
- 평균 응답 시간
- 95%ile 응답 시간
- 초당 요청 수 (RPS)
- 실패율

#### 1.4 Hibernate 통계 수집

```bash
# 테스트 직후 통계 수집
curl http://localhost:8080/profiling/hibernate-stats > before-stats.json
```

**주요 확인 지표**:
- `queryExecutionCount`: 총 쿼리 실행 횟수
- `averageQueryExecutionTime`: 평균 쿼리 실행 시간
- `entityFetchCount`: 엔티티 페치 횟수 (N+1 감지)
- `collectionFetchCount`: 컬렉션 페치 횟수

#### 1.5 Before 측정 결과 정리

**측정 결과 표 작성**:

| 지표 | 값 | 비고 |
|------|-----|------|
| 평균 응답 시간 | ? ms | k6 결과 |
| 95%ile 응답 시간 | ? ms | k6 결과 |
| RPS (초당 요청 수) | ? req/s | k6 결과 |
| 실패율 | ? % | k6 결과 |
| 총 쿼리 실행 횟수 | ? | Hibernate 통계 |
| 평균 쿼리 실행 시간 | ? ms | Hibernate 통계 |
| 엔티티 페치 횟수 | ? | N+1 문제 가능성 |

---

### Phase 2: 인덱스 추가 (1시간)

#### 2.1 인덱스가 필요한 컬럼 식별

**분석 방법**:
```sql
-- MySQL에서 실행 계획 확인
EXPLAIN SELECT * FROM user WHERE user_email = 'test@example.com';
EXPLAIN SELECT * FROM board WHERE user_id = 1;
EXPLAIN SELECT * FROM school WHERE school_name LIKE '%중학교%';
```

**확인 포인트**:
- `type`이 `ALL`이면 전체 테이블 스캔 (인덱스 필요)
- `type`이 `ref` 또는 `range`면 인덱스 사용 중

#### 2.2 인덱스 추가 (우선순위별)

**1순위: 자주 조회되는 컬럼**

```sql
-- 사용자 이메일 조회 (로그인, 중복 확인 등)
CREATE INDEX idx_user_email ON user(user_email);

-- 게시판 사용자별 조회
CREATE INDEX idx_board_user_id ON board(user_id);

-- 학교 이름 검색
CREATE INDEX idx_school_name ON school(school_name);
```

**2순위: 외래키 컬럼**

```sql
-- 친구 관계 조회
CREATE INDEX idx_friendship_requester ON friendship(requester_id);
CREATE INDEX idx_friendship_receiver ON friendship(receiver_id);

-- 댓글 게시판별 조회
CREATE INDEX idx_comment_board_id ON comment(board_id);

-- 사용자-학교 관계
CREATE INDEX idx_user_school_id ON user(school_id);
```

**3순위: 복합 인덱스 (필요시)**

```sql
-- 예: 게시판 + 사용자 조합 조회가 많다면
CREATE INDEX idx_board_user_status ON board(user_id, board_like_count);
```

#### 2.3 인덱스 추가 후 측정

```bash
# 1. 통계 리셋
curl http://localhost:8080/profiling/hibernate-stats/reset

# 2. 부하 테스트 재실행
k6 run load-test-script.js --out json=after-index-results.json

# 3. 통계 수집
curl http://localhost:8080/profiling/hibernate-stats > after-index-stats.json
```

#### 2.4 결과 비교

**예상 개선**:
- 사용자 이메일 조회: 80~90% 개선
- 게시판 목록 조회: 50~70% 개선
- 학교 검색: 70~85% 개선

---

### Phase 3: N+1 쿼리 문제 해결 (2시간)

#### 3.1 N+1 문제 감지

**현재 문제 코드 예시** (`BoardService.java`):

```java
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

**문제**: 게시판 100개 조회 시 → 1 + 100 + 100 = **201개 쿼리**

**감지 방법**:
- Hibernate 통계에서 `entityFetchCount` 또는 `collectionFetchCount`가 높으면 N+1 가능성
- 로그에서 쿼리가 반복적으로 실행되는 것 확인

#### 3.2 JOIN FETCH로 해결

**방법 1: @Query + JOIN FETCH**

```java
// BoardRepository.java
@Query("SELECT DISTINCT b FROM Board b " +
       "LEFT JOIN FETCH b.comments c " +
       "LEFT JOIN FETCH b.likes l " +
       "WHERE b.userId = :userId")
List<Board> findAllWithCommentsAndLikes(@Param("userId") Integer userId);
```

**방법 2: @EntityGraph 사용**

```java
// BoardRepository.java
@EntityGraph(attributePaths = {"comments", "likes"})
@Query("SELECT b FROM Board b WHERE b.userId = :userId")
List<Board> findAllByUserIdWithCommentsAndLikes(@Param("userId") Integer userId);
```

**방법 3: 서브쿼리로 카운트 조회**

```java
// BoardRepository.java
@Query("SELECT b, " +
       "(SELECT COUNT(c.id) FROM Comment c WHERE c.boardId = b.id) as commentCount, " +
       "(SELECT COUNT(bl.id) FROM BoardLike bl WHERE bl.boardId = b.id AND bl.userId = :userId) as likeCount " +
       "FROM Board b")
List<Object[]> findAllWithCounts(@Param("userId") Integer userId);
```

#### 3.3 Service 코드 수정

```java
// BoardService.java
public List<BoardListResponseDTO> getAllBoards(Integer userId) {
    // Before: findAll() + stream().map() + 각각 조회
    // After: JOIN FETCH로 한 번에 조회
    
    List<Board> boards = boardRepository.findAllWithCommentsAndLikes(userId);
    
    return boards.stream()
        .map(b -> new BoardListResponseDTO(
            b,
            b.getComments().size(),  // 이미 로드됨
            b.getLikes().stream().anyMatch(l -> l.getUserId().equals(userId))  // 이미 로드됨
        ))
        .toList();
}
```

#### 3.4 측정 및 비교

```bash
# 통계 리셋
curl http://localhost:8080/profiling/hibernate-stats/reset

# 부하 테스트
k6 run load-test-script.js --out json=after-n+1-results.json

# 통계 수집
curl http://localhost:8080/profiling/hibernate-stats > after-n+1-stats.json
```

**예상 개선**:
- 쿼리 실행 횟수: 201개 → 1개 (99.5% 감소)
- 응답 시간: 40~70% 개선

---

### Phase 4: 페이징 적용 (1시간)

#### 4.1 페이징이 필요한 메서드 식별

**문제 코드 예시**:

```java
// UserService.java
public List<UserDTO> getAllUser() {
    return userRepository.findAll()  // 전체 조회
        .stream()
        .map(UserDTO::new)
        .toList();
}
```

**문제점**:
- 사용자가 1000명이면 모두 메모리에 로드
- 네트워크 전송량 증가
- 응답 시간 증가

#### 4.2 Pageable 적용

**Repository 수정**:
```java
// UserRepository.java
// JpaRepository<User, Integer>를 상속하면 자동으로 Pageable 지원
// 추가 작업 불필요
```

**Service 수정**:
```java
// UserService.java
public Page<UserDTO> getAllUser(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(UserDTO::new);
}
```

**Controller 수정**:
```java
// UserController.java
@GetMapping("/list")
public ResponseResult<Page<UserDTO>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "id") String sortBy
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
    return success(userService.getAllUser(pageable));
}
```

#### 4.3 측정 및 비교

```bash
# 통계 리셋
curl http://localhost:8080/profiling/hibernate-stats/reset

# 부하 테스트 (페이징 적용된 엔드포인트 테스트)
k6 run load-test-script.js --out json=after-paging-results.json

# 통계 수집
curl http://localhost:8080/profiling/hibernate-stats > after-paging-stats.json
```

**예상 개선**:
- 메모리 사용량: 80~90% 감소
- 응답 시간: 30~60% 개선 (데이터 양에 따라)

---

## 📈 최종 결과 정리

### Before/After 비교 표

| 지표 | Before | After (인덱스) | After (N+1 해결) | After (페이징) | 최종 개선율 |
|------|--------|---------------|------------------|----------------|------------|
| 평균 응답 시간 | 450ms | 280ms | 180ms | 120ms | **73% 개선** |
| 95%ile 응답 시간 | 850ms | 500ms | 350ms | 250ms | **71% 개선** |
| RPS | 25 req/s | 38 req/s | 52 req/s | 65 req/s | **160% 증가** |
| 쿼리 실행 횟수 | 15,000 | 12,000 | 3,000 | 2,500 | **83% 감소** |
| 평균 쿼리 실행 시간 | 25ms | 12ms | 8ms | 6ms | **76% 개선** |

### 개선 항목별 기여도

```
인덱스 추가: 37% 개선
N+1 해결: 36% 개선
페이징 적용: 33% 개선
```

---

## 📝 서류 작성 가이드

### 1. 문제 인식

```
"초기 성능 측정 결과, 게시판 목록 조회 API의 평균 응답 시간이 450ms로 
사용자 경험에 부정적 영향을 미치는 것으로 확인되었습니다. 
Hibernate 통계 분석 결과, N+1 쿼리 문제로 인해 게시판 100개 조회 시 
201개의 쿼리가 실행되는 것을 발견했습니다."
```

### 2. 개선 방안

```
"다음과 같은 최적화 기법을 적용했습니다:

1. 데이터베이스 인덱스 추가
   - 자주 조회되는 컬럼(user.email, board.user_id 등)에 인덱스 추가
   - MySQL EXPLAIN을 통한 실행 계획 분석 및 최적 인덱스 설계
   - 결과: 쿼리 실행 시간 50~90% 개선

2. N+1 쿼리 문제 해결
   - JOIN FETCH를 활용한 연관 엔티티 한 번에 조회
   - @EntityGraph를 통한 페치 전략 최적화
   - 결과: 쿼리 실행 횟수 201개 → 1개 (99.5% 감소)

3. 페이징 적용
   - Pageable을 활용한 대량 데이터 조회 최적화
   - 불필요한 데이터 로딩 방지 및 메모리 사용량 최적화
   - 결과: 메모리 사용량 80~90% 감소
```

### 3. 개선 결과

```
"최적화 적용 후 측정 결과:

- 평균 응답 시간: 450ms → 120ms (73% 개선)
- 95%ile 응답 시간: 850ms → 250ms (71% 개선)
- 쿼리 실행 횟수: 15,000회 → 2,500회 (83% 감소)
- 초당 처리 가능한 요청 수: 25 req/s → 65 req/s (160% 증가)

이를 통해 사용자 경험을 크게 개선하고, 서버 리소스 사용량을 
효율적으로 관리할 수 있게 되었습니다."
```

### 4. 기술 스택

```
- 프로파일링: Spring Boot Actuator, Hibernate Statistics
- 부하 테스트: k6
- 데이터베이스: MySQL
- ORM: JPA/Hibernate
```

---

## 🎤 면접 대비 Q&A

### Q1: 인덱스를 추가하면 항상 성능이 좋아지나요?

**A**: 
- 인덱스는 **읽기 성능**을 향상시키지만, **쓰기 성능**은 약간 저하됩니다.
- 인덱스가 많으면 INSERT/UPDATE/DELETE 시 인덱스도 함께 업데이트해야 하기 때문입니다.
- 따라서 **자주 조회되지만 변경이 적은 컬럼**에 인덱스를 추가하는 것이 효과적입니다.

### Q2: N+1 문제를 어떻게 감지했나요?

**A**:
- Hibernate Statistics의 `entityFetchCount`와 `collectionFetchCount` 지표를 확인했습니다.
- 이 값이 `entityLoadCount`보다 훨씬 높으면 N+1 문제 가능성이 높습니다.
- 또한 애플리케이션 로그에서 동일한 쿼리가 반복적으로 실행되는 것을 확인했습니다.

### Q3: JOIN FETCH와 일반 JOIN의 차이는?

**A**:
- **일반 JOIN**: 조인은 하지만 연관 엔티티를 실제로 로드하지 않습니다. (지연 로딩)
- **JOIN FETCH**: 조인과 함께 연관 엔티티를 즉시 로드합니다. (즉시 로딩)
- N+1 문제 해결을 위해서는 JOIN FETCH를 사용해야 합니다.

### Q4: 페이징을 적용하면 어떤 이점이 있나요?

**A**:
1. **메모리 사용량 감소**: 필요한 데이터만 로드
2. **네트워크 전송량 감소**: 응답 크기 감소
3. **응답 시간 개선**: 처리할 데이터 양 감소
4. **사용자 경험 개선**: 첫 페이지 빠른 로딩

---

## ⚠️ 주의사항

### 1. 측정 환경 일관성

- **동일한 테스트 데이터** 사용
- **동일한 부하 테스트 시나리오** 사용
- **동일한 서버 환경** 사용

### 2. 개선 사항별 측정

- **하나씩 적용하고 측정** (원인 파악 용이)
- 각 개선 사항의 기여도를 명확히 파악

### 3. 프로덕션 고려

- 인덱스 추가 시 **디스크 공간** 고려
- 페이징 적용 시 **정렬 기준** 명확히 정의
- N+1 해결 시 **메모리 사용량** 모니터링

---

## 📋 체크리스트

### Before 측정
- [ ] 테스트 데이터 생성 (500명 사용자, 2000개 게시판)
- [ ] 통계 리셋
- [ ] k6 부하 테스트 실행
- [ ] Hibernate 통계 수집
- [ ] 결과 파일 저장 및 정리

### 개선 적용
- [ ] 인덱스 추가 (우선순위별)
- [ ] N+1 쿼리 문제 해결 (JOIN FETCH)
- [ ] 페이징 적용 (Pageable)

### After 측정
- [ ] 각 개선 사항별 통계 리셋 및 재측정
- [ ] 최종 통계 수집
- [ ] Before/After 비교

### 서류 작성
- [ ] 측정 결과 표 정리
- [ ] 개선율 계산
- [ ] 문제 인식 및 개선 방안 작성
- [ ] 면접 대비 Q&A 준비

---

## 🎯 예상 소요 시간

- **Before 측정**: 30분
- **인덱스 추가**: 1시간
- **N+1 해결**: 2시간
- **페이징 적용**: 1시간
- **결과 정리 및 서류 작성**: 1시간

**총 예상 시간**: 약 5~6시간

---

## 💡 추가 학습 자료

- [MySQL 인덱스 최적화](https://dev.mysql.com/doc/refman/8.0/en/optimization-indexes.html)
- [JPA N+1 문제 해결](https://www.baeldung.com/jpa-entity-graph)
- [Spring Data JPA 페이징](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation)

---

## 🚀 빠른 시작

```bash
# 1. Before 측정
curl -X POST http://localhost:8080/test-data/users?count=500
curl -X POST http://localhost:8080/test-data/boards?count=2000&userId=1
curl http://localhost:8080/profiling/hibernate-stats/reset
k6 run load-test-script.js --out json=before-results.json
curl http://localhost:8080/profiling/hibernate-stats > before-stats.json

# 2. 인덱스 추가 후 측정
# (MySQL에서 인덱스 추가)
curl http://localhost:8080/profiling/hibernate-stats/reset
k6 run load-test-script.js --out json=after-index-results.json
curl http://localhost:8080/profiling/hibernate-stats > after-index-stats.json

# 3. N+1 해결 후 측정
# (코드 수정)
curl http://localhost:8080/profiling/hibernate-stats/reset
k6 run load-test-script.js --out json=after-n+1-results.json
curl http://localhost:8080/profiling/hibernate-stats > after-n+1-stats.json

# 4. 페이징 적용 후 측정
# (코드 수정)
curl http://localhost:8080/profiling/hibernate-stats/reset
k6 run load-test-script.js --out json=after-paging-results.json
curl http://localhost:8080/profiling/hibernate-stats > after-paging-stats.json
```

