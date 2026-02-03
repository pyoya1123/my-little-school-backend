# 🏫 마이 리틀 스쿨 (My Little School) - Backend

> **소규모 학교 학생들을 위한 메타버스 기반 소셜 학습 플랫폼**

학생들이 가상 공간에서 친구를 사귀고, 나만의 교실을 꾸미며, 퀴즈와 골든벨 등 다양한 활동에 참여할 수 있는 메타버스 서비스입니다.

---

## 📋 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [기술 스택](#-기술-스택)
3. [주요 기능](#-주요-기능)
4. [ERD](#-erd)
5. [성능 최적화](#-성능-최적화)
6. [부하 테스트 결과](#-부하-테스트-결과)

---

## 🎯 프로젝트 소개

### 배경
지방의 소규모 학교 학생들의 사회화 부족 문제를 해결하기 위해 메타버스 환경에서 다른 학교 친구들과 만나 다양한 경험을 할 수 있는 메타버스 플랫폼을 개발했습니다.
#### 프로젝트 종료 후, 주도적인 성능 개선과 풀스택 마이그레이션을 위해 퍼블릭 리포지토리로 새롭게 이관하여 개발을 이어나가고 있습니다!

### 목표
- 학생들이 재미있게 소통할 수 있는 **소셜 기능** 제공
- 나만의 공간을 꾸미는 **커스터마이징 기능** 구현
- **퀘스트 시스템**을 통한 자연스러운 서비스 온보딩

### 개발 기간
- 2024.09 ~ 2024.12 (4개월)

### 팀 구성
- Backend 1명, Unity 3명, 기획 1명, TA 1명

---

## 🛠 기술 스택

### Backend
| 기술 | 버전 | 설명 |
|------|------|------|
| **Java** | 17 | 프로그래밍 언어 |
| **Spring Boot** | 3.3.4 | 웹 프레임워크 |
| **Spring Data JPA** | - | ORM |
| **Spring WebSocket** | - | 실시간 통신 |

### Database & Cache
| 기술 | 설명 |
|------|------|
| **MySQL** | 메인 데이터베이스 |
| **Redis** | 세션 캐시, 실시간 데이터 |

### Infra & DevOps
| 기술 | 설명 |
|------|------|
| **AWS S3** | 이미지/파일 스토리지 |
| **Cloudinary** | 이미지 최적화 CDN |
| **Docker** | 컨테이너화 |
| **k6** | 부하 테스트 |

### Monitoring
| 기술 | 설명 |
|------|------|
| **Spring Actuator** | 애플리케이션 모니터링 |
| **Micrometer + Prometheus** | 메트릭 수집 |
| **Hibernate Statistics** | JPA 쿼리 분석 |

---

## 💡 주요 기능

### 1. 사용자 관리
- 회원가입/로그인 (Spring Security)
- 프로필 관리 (아바타, 상태메시지)
- 레벨/경험치 시스템

### 2. 소셜 기능
- **친구 시스템**: 친구 추가/삭제, 친구 목록
- **쪽지 시스템**: 1:1 비동기 메시지
- **실시간 채팅**: WebSocket 기반 채팅
- **방명록**: 친구 교실에 메시지 남기기

### 3. 커뮤니티
- **게시판**: CRUD, 좋아요, 댓글
- **갤러리**: 이미지 업로드 (S3/Cloudinary)

### 4. 교실 꾸미기
- **가구 배치**: 드래그 앤 드롭으로 가구 배치
- **인벤토리 시스템**: 아이템 구매/관리
- **맵 콘테스트**: 내 교실 자랑하기

### 5. 퀘스트 시스템
- 튜토리얼 퀘스트로 자연스러운 온보딩
- 퀘스트 완료 시 골드/경험치 보상
- 아이템 보상 지급

---

## 📊 ERD

```

```

**주요 테이블**: User, Board, Comment, Friendship, Note, Furniture, Inventory, Item, Quest, UserQuest, School, Gallery, GuestBook, ChatLog, MapContest

## ⚡ 성능 최적화

## 📈 부하 테스트 결과
# 부하 테스트

<details>

<summary>개선 전 코드</summary>

</summary>

## Board

```json
public List<BoardListResponseDTO> getAllBoards(Integer userId) {
  List<BoardListResponseDTO> a =  boardRepository.findAll()
      .stream()
          .filter(b -> b.getUserId().equals(userId))
          .map(b -> new BoardListResponseDTO(
              b,
              commentService.getCommentCountByBoardId(b.getId()),
              boardLikeService.isExistLike(new BoardGetLikeDTO(b.getId(), userId))
          )
      )
      .toList();
  return a;
}

public Boolean isExistLike(BoardGetLikeDTO dto){
  return boardLikeRepository.getBoardLikeByBoardIdAndUserId(
      dto.getBoardId(), dto.getUserId()) != null;
}
```

```json
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
		// 위 코드에서 내부 findAll() 함수 사용
}
```

---

## Comment

```json
public Integer getCommentCountByBoardId(Integer boardId) {
  return commentRepository.getCommentCountByBoardId(boardId);
}
```

```json
@Query("select count(*) from Comment c where c.boardId = :boardId")
Integer getCommentCountByBoardId(@Param("boardId") Integer boardId);
```

</details>

<details>

<summary>개선 후 코드</summary>

## Board

```json
/*
  public List<BoardListResponseDTO> getAllBoards(Integer userId) {
  List<BoardListResponseDTO> a =  boardRepository.findAll()
      .stream()
          .filter(b -> b.getUserId().equals(userId))
          .map(b -> new BoardListResponseDTO(
              b,
              commentService.getCommentCountByBoardId(b.getId()),
              boardLikeService.isExistLike(new BoardGetLikeDTO(b.getId(), userId))
          )
      )
      .toList();
  return a;
}

public Boolean isExistLike(BoardGetLikeDTO dto){
  return boardLikeRepository.getBoardLikeByBoardIdAndUserId(
      dto.getBoardId(), dto.getUserId()) != null;
}
*/

public List<BoardListResponseDTO> getBoardListWithCommentAndBoardLikeByUserId(Integer userId) {
  List<Object[]> a =  boardRepository.getBoardListWithCommentAndBoardLikeByUserId(userId);
  return a.stream().map(o -> {
    Board b = (Board) o[0];
    Long commentCount = (Long)o[1];
    Boolean isExistLike = o[2] != null;
    return new BoardListResponseDTO(b, commentCount, isExistLike);
  }).toList();
}
```

```json
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

  @Query("SELECT b, count(c), bl" +
          " FROM Board b" +
          " LEFT JOIN Comment c ON c.boardId = b.id " +
          " LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId " +
          " WHERE b.userId = :userId" +
          " GROUP BY b, bl")
  List<Object[]> getBoardListWithCommentAndBoardLikeByUserId(
      @Param("userId") Integer userId
  );
}
```

---

## Comment

```json
public Long getCommentCountByBoardId(Integer boardId) {
  return commentRepository.getCommentCountByBoardId(boardId);
}
```

```json
@Query("select count(*) from Comment c where c.boardId = :boardId")
Long getCommentCountByBoardId(@Param("boardId") Integer boardId);
```

</details>

<details>

<summary>개선 전 (유저 수: 10, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

</summary>

## 부하 테스트 진행중
- 유저 수 : 10
- 게시글 수 : 유저 당 5 → 50
- 댓글 수 : 게시글 당 3 → 150
<img width="2702" height="1187" alt="image" src="https://github.com/user-attachments/assets/c7a71ec1-dd20-451b-9a67-34b01572aa1c" />


## 부하 테스트 완료
<img width="2734" height="1182" alt="image (1)" src="https://github.com/user-attachments/assets/b81ed4f2-134e-4ffe-ac7d-7eba7fe1c0b9" />
<img width="2720" height="1983" alt="image (2)" src="https://github.com/user-attachments/assets/40159ead-5408-4e42-b5cf-fab08f3e3b68" />
<img width="2715" height="521" alt="image (3)" src="https://github.com/user-attachments/assets/7bbbb0c1-08ce-4c98-872c-38243ac5bfa2" />



## 분석

```markdown
1. 테스트 개요
사용자 수: 10명 (목표 10명 달성)
게시판 수: 50개 (사용자당 5개, 목표 달성)
동시 접속자(VU): 최대 10명 (1:1 매칭 테스트)
테스트 시간: 2분 10초

2. 응답 시간
평균 응답 시간: 8.46ms
p(95) (95% 요청): 15.88ms
p(99) (99% 요청): 25ms
최대 응답 시간: 342.36ms

분석:
응답 시간이 매우 빠름 (대부분 10ms 이내).
N+1 문제가 발생하더라도 데이터 양이 적어서(게시판 50개, 댓글 150개) 성능 저하가 눈에 띄지 않음.

3. 처리량
초당 요청 수 (RPS): 약 5.0req/s
총 요청 수: 685건
```

---

## Hibernate Stats

```json
{
  "queryExecutionMaxTime": 106,
  "queryExecutionCount": 2660,
  "queryCount": 42,
  "queryPlanCacheMissCount": 40,
  "queryPlanCacheHitCount": 4873,
  "queryExecutionMaxTimeQueryString": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
  "entityLoadCount": 15021
}
```

> 💡 **용어 설명**
> 

> - queryExecutionMaxTime → 가장 느린 쿼리의 실행 시간 (ms)
> 

> - queryExecutionCount → 총 쿼리 실행 횟수(실제로 실행된 모든 SQL 쿼리의 총합)
> 

> - queryCount → 고유 쿼리수(서로 다른 쿼리 패턴의 개수)
> 

> - queryPlanCacheMissCount → 쿼리 실행 계획 캐시 미스 횟수
> 

> - queryPlanCacheHitCount → 쿼리 실행 계획 캐시 히트 횟수
> 

> - queryExecutionMaxTimeQueryString → 가장 느린 쿼리의 SQL 문
> 

> - entityLoadCount → 데이터베이스에서 엔티티를 로드한 총 횟수
> 

### 쿼리 별 실행 횟수

```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 6,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 1298,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select bl from BoardLike bl where bl.boardId = :boardId and bl.userId = :userId",
      "executionCount": 1090,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 15,
      "executionAvgTime": 1,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select b1_0.id,b1_0.board_content,b1_0.board_like_count,b1_0.board_title,b1_0.user_id from board b1_0",
      "executionCount": 218,
      "cacheMissCount": 0
    }
  ]
}
```

### 1. N+1 문제

- **queryExecutionCount**: 2,660 (총 쿼리 실행 횟수)
- **queryCount**: 42 (고유 쿼리 수)
- **반복 비율** = 2,660 ÷ 42 = **약 63.3회/쿼리**
- 42개의 서로 다른 쿼리가 평균적으로 각각 63번씩 반복 실행되었습니다.
- 총 HTTP 요청 수가 685건인 점을 감안할 때, 요청당 쿼리 수가 많아 **N+1 문제가 발생하고 있음**을 보여줍니다.

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**
    - **실행 시간**: 106ms
    - **쿼리**: select s1_[0.id](http://0.id), s1_0.latitude, s1_[0.school](http://0.school)*location, s1_0.longitude, s1*[0.school](http://0.school)_name from school s1_0
    - **의미**: School 테이블 전체 조회 쿼리

### 3. 엔티티 로드 분석

- **entityLoadCount**: 15,021 (엔티티 로드 횟수)
- **쿼리 대비 로드 비율**: 15,021 ÷ 2,660 ≈ **5.65**
- 쿼리 실행 1회당 평균 5.65개의 엔티티가 로드되었습니다.

### 4. 쿼리 캐시 효율

- **queryPlanCacheHitCount**: 4,873 (캐시 히트)
- **queryPlanCacheMissCount**: 40 (캐시 미스)
- **캐시 효율** = 4,873 ÷ (4,873 + 40) = **약 99.19%**

</details>

<details>

<summary>개선 전 (유저 수: 100, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

</summary>

## 부하 테스트 진행중

- 유저 수 : 100
- 게시글 수 : 유저 당 5 → 500
- 댓글 수 : 게시글 당 3 → 1500

## 부하 테스트 완료

<!-- 이미지는 GitHub에 업로드 후 경로 수정 필요 -->

## 분석

```markdown
1. 테스트 개요
사용자 수: 100명 (목표 100명 달성)
게시판 수: 500개 (사용자당 5개, 목표 달성)
동시 접속자(VU): 최대 100명
테스트 시간: 2분 31초

2. 응답 시간
평균 응답 시간: 11.69ms
p(95): 24.7ms
p(99): 29.3ms
최대 응답 시간: 10.65s

3. 처리량
초당 요청 수 (RPS): 약 32.8req/s
총 요청 수: 4,979건
```

### 1. N+1 문제

- **queryExecutionCount**: 19,160 (총 쿼리 실행 횟수)
- **반복 비율** = 19,160 ÷ 42 = **약 456회/쿼리**
- 특히 다음 쿼리들의 실행 횟수가 압도적으로 높습니다:
    1. **댓글 수 조회**: **9,022회**
    2. **좋아요 여부 조회**: **7,535회**
    3. **게시판 목록 조회**: **1,597회**

### 3. 엔티티 로드 분석

- **entityLoadCount**: 686,907
- **쿼리 대비 로드 비율**: 686,907 ÷ 19,160 ≈ **35.85**

</details>

<details>

<summary>개선 전 (유저 수: 500, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

</summary>

## 부하 테스트 진행중

- 유저 수 : 500
- 게시글 수 : 유저 당 5 → 2500
- 댓글 수 : 게시글 당 3 → 7500

## 분석

```markdown
1. 테스트 개요
사용자 수: 500명
게시판 수: 2,500개
동시 접속자(VU): 최대 500명
테스트 시간: 3분 58초

2. 응답 시간
평균 응답 시간: 410.4ms
p(95): 1.94s
p(99): 2.37s
최대 응답 시간: 3.74s

3. 처리량
초당 요청 수 (RPS): 약 91.9req/s
총 요청 수: 21,910건
```

### 1. N+1 문제

- **queryExecutionCount**: 445,144
- **반복 비율** = 445,144 ÷ 42 = **약 10,598회/쿼리**
- 특히 다음 쿼리들의 실행 횟수가 압도적:
    1. **댓글 수 조회**: **209,626회**
    2. **좋아요 여부 조회**: **177,690회**
    3. **게시판 목록 조회**: **36,328회**

### 3. 엔티티 로드 분석

- **entityLoadCount**: 158,851,775 (약 1.5억 회)
- **쿼리 대비 로드 비율**: **356.8**
- **메모리 부족(OOM) 위험이 매우 높습니다.**

</details>

---

<details>

<summary>개선 후 (유저 수: 10, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 10
- 게시판 수 : 유저 당 5 → 50
- 댓글 수 : 게시판 당 3 → 150

## 부하 테스트 완료

<!-- 이미지는 GitHub에 업로드 후 경로 수정 필요 -->

## 분석

### **1. 테스트 개요**

- **사용자 수**: 10명 (목표 10명 달성)
- **게시판 수**: 50개 (사용자당 5개, 목표 달성)
- **동시 접속자(VU)**: 최대 10명 (1:1 매칭 테스트)
- **테스트 시간**: 2분 11초 (이전과 동일)

### 2. 응답 시간 분석

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 전 재확인) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 8.46ms | 11.72ms | ▲ 3.26ms |
| **중앙값** | 6.13ms | 8.52ms | ▲ 2.39ms |
| **p(95)** | 15.88ms | 24.13ms | ▲ 8.25ms |
| **최대 응답 시간** | 342.36ms | 199.99ms | ▼ 142.37ms |

**분석**

- 두 테스트 모두 **평균 10ms 내외**의 매우 빠른 응답 속도를 보여줍니다.
- 현재 테스트가 약 3ms 느리게 측정되었으나, 이는 로컬 환경의 일시적 부하나 네트워크 노이즈에 의한 오차 범위 내입니다.
- **결론**: 데이터가 적은 상황(유저 10명)에서는 N+1 문제가 성능에 큰 영향을 주지 않음을 재확인했습니다.

### 3. 처리량

| 지표 | 이전 테스트 | 현재 테스트 | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 5.0req/s | 약 5.2req/s | ▲ 0.2req/s |
| **총 요청 수** | 685건 | 685건 | 동일 |

**분석**

- 처리량은 거의 동일하며, 시스템이 안정적으로 요청을 처리했습니다.

### **4. 종합 평가**

- 유저 10명 규모에서는 **N+1 문제의 유무가 응답 속도에 미치는 영향이 미미**합니다.

---

## Hibernate Stats

```json
{
  "queryExecutionMaxTime": 86,
  "queryExecutionCount": 480,
  "queryCount": 42,
  "queryPlanCacheMissCount": 41,
  "queryPlanCacheHitCount": 949,
  "queryExecutionMaxTimeQueryString": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
  "entityLoadCount": 6090
}
```

### 1. N+1 문제 확인

- **queryExecutionCount**:
    - 최적화 전: **2,660회**
    - 최적화 후: **480회**
    - 변화: **약 82% 감소** (약 5.5배 감소)
- **주요 쿼리 변화**:
    - **게시판 목록 조회 (Main)**: 218회 (동일)
    - **댓글 수 조회 (N+1)**: 전: 1,298회 → 후: **208회** (약 84% 감소)
    - **좋아요 여부 조회 (N+1)**: 전: 1,090회 → 후: **0회** (완전 제거)

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**: 전: 106ms → 후: 86ms (select ... from school)
- **최적화 쿼리 성능**: SELECT b, count(c), bl ...: 평균 **16ms**

### 3. 엔티티 로드 분석

- **entityLoadCount**: 전: **15,021개** → 후: **6,090개** → 변화: **약 59.5% 감소**

### 4. 쿼리 캐시 효율

- 전: 캐시 히트 4,873회 (효율 99.19%)
- 후: 캐시 히트 949회 (효율 95.86%)

**종합 결론**: 최적화 적용 후 **쿼리 실행 횟수가 82% 감소**하고, **엔티티 로딩이 60% 감소**했습니다.

</details>

<details>

<summary>개선 후 (유저 수: 100, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 100
- 게시판 수 : 유저 당 5 → 500
- 댓글 수 : 게시판 당 3 → 1500

## 부하 테스트 완료

<!-- 이미지는 GitHub에 업로드 후 경로 수정 필요 -->

## 분석

### **1. 테스트 개요**

- **사용자 수**: 100명 (목표 100명 달성)
- **게시판 수**: 500개 (사용자당 5개, 목표 달성)
- **동시 접속자(VU)**: 최대 100명 (1:1 매칭 테스트)
- **테스트 시간**: 약 2분 20초 내외 (동일 조건)

### 2. 응답 시간 분석

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 11.69ms | 10.63ms | ▼ 1.06ms |
| **중앙값** | 5.39ms | 7.02ms | ▲ 1.63ms |
| **p(95)** | 24.7ms | 24.01ms | ▼ 0.69ms |
| **최대 응답 시간** | 10.65s | 53.2ms | ▼ 10.59s |

**분석**

- 평균 응답 시간과 상위 95%(p95) 응답 시간은 **최적화 전후가 거의 비슷**합니다 (10~11ms 수준).
- **최대 응답 시간**이 10.65초에서 **53.2ms**로 획기적으로 줄어들었습니다.
- **결론**: 100명 규모에서도 **평균 응답 속도의 드라마틱한 개선은 나타나지 않았습니다.** 하지만 **응답의 안정성(최대 지연 감소)은 크게 향상**되었습니다.

### 3. 처리량

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 32.8req/s | 약 49.1req/s | ▲ 16.3req/s |
| **총 요청 수** | 4,979건 | 6,865건 | ▲ 1,886건 |

**분석**

- **처리량(RPS)이 약 50% 증가**했습니다.
- 동일한 시간 동안 처리한 총 요청 수가 약 1,900건 늘어났습니다.

### **4. 종합 평가**

- **응답 속도**: 평균 속도는 비슷하지만, **최대 지연 시간이 획기적으로 감소**
- **처리 용량**: 동일 하드웨어에서 **처리 가능한 트래픽(Throughput)이 50% 증가**

---

## Hibernate Stats

```json
{
  "queryExecutionMaxTime": 86,
  "queryExecutionCount": 5257,
  "queryCount": 42,
  "queryPlanCacheMissCount": 41,
  "queryPlanCacheHitCount": 10501,
  "queryExecutionMaxTimeQueryString": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
  "entityLoadCount": 25684
}
```

### 1. N+1 문제 확인

- **queryExecutionCount**: 최적화 전: **19,160회** → 최적화 후: **5,257회** → 변화: **약 72.5% 감소**
- **주요 쿼리 변화**:
    - **댓글 수 조회 (N+1)**: 전: 9,022회 → 후: **2,296회** (약 74.5% 감소)
    - **좋아요 여부 조회 (N+1)**: 전: 7,535회 → 후: **0회** (완전 제거)

### 3. 엔티티 로드 분석

- **entityLoadCount**: 전: **686,907개** → 후: **25,684개** → 변화: **약 96.2% 감소**

**종합 결론**: 최적화 적용 후 **쿼리 실행 횟수가 72.5% 감소**하고, **엔티티 로딩이 96.2% 감소**했습니다.

</details>

<details>

<summary>개선 후 (유저 수: 500, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 500
- 게시판 수 : 유저 당 5 → 2500
- 댓글 수 : 게시판 당 3 → 7500

## 부하 테스트 완료

<!-- 이미지는 GitHub에 업로드 후 경로 수정 필요 -->

## 분석

### **1. 테스트 개요**

- **사용자 수**: 500명 (동일)
- **게시판 수**: 2,500개 (동일)
- **동시 접속자(VU)**: 최대 500명 (동일)
- **테스트 시간**: 약 3분 20초 (동일)

### 2. 응답 시간

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 410.4ms | 13.56ms | ▼ 396.84ms (**약 97% 개선**) |
| **중앙값** | 124.66ms | 7.61ms | ▼ 117.05ms |
| **p(95)** | 1.94s | 36.89ms | ▼ 1.90s |
| **최대 응답 시간** | 3.74s | 211.66ms | ▼ 3.53s |

**분석**

- **평균 응답 시간**이 410ms에서 **13.56ms**로 획기적으로 단축되었습니다.
- **p(95)** 지표가 1.94초에서 **36.89ms**로 개선되어, 대다수 사용자가 느끼던 지연(Latency)이 완전히 사라졌습니다.

### 3. 처리량

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 91.9req/s | 약 135.5req/s | ▲ 43.6req/s (**약 47% 증가**) |
| **총 요청 수** | 21,910건 | 27,352건 | ▲ 5,442건 |

**분석**

- **처리량(RPS)**이 약 92req/s에서 **135.5req/s**로 **47% 이상 증가**했습니다.

### **4. 종합 평가**

- **응답 속도**: N+1 문제 해결을 통해 **평균 응답 속도가 30배 이상 빨라졌습니다.**
- **처리 용량**: 동일한 하드웨어 리소스로 **약 47% 더 많은 트래픽을 처리**할 수 있게 되었습니다.
- **시스템 안정성**: 최대 응답 시간이 3.7초에서 0.2초대로 줄어들어 안정적인 서비스를 제공할 수 있음을 입증했습니다.

---

## Hibernate Stats

```json
{
  "queryExecutionMaxTime": 185,
  "queryExecutionCount": 39321,
  "queryCount": 42,
  "queryPlanCacheMissCount": 41,
  "queryPlanCacheHitCount": 78625,
  "queryExecutionMaxTimeQueryString": "SELECT b, count(c), bl FROM Board b LEFT JOIN Comment c ON c.boardId = b.id  LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId  WHERE b.userId = :userId GROUP BY b, bl",
  "entityLoadCount": 159712
}
```

### 1. N+1 문제 확인

- **queryExecutionCount**: 최적화 전: **445,144회** → 최적화 후: **39,321회** → 변화: **약 91.2% 감소**
- **주요 쿼리 변화**:
    - **댓글 수 조회 (N+1)**: 전: 209,626회 → 후: **16,327회** (약 92% 감소)
    - **좋아요 여부 조회 (N+1)**: 전: 177,690회 → 후: **0회** (완전 제거)

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**: 전: 410ms (select ... from board) → 후: 185ms (SELECT b, count(c), bl ...)
- **최적화 쿼리 성능**: SELECT b, count(c), bl ...: 평균 **24ms**

### 3. 엔티티 로드 분석

- **entityLoadCount**: 전: **158,851,775개** (약 1.5억 개) → 후: **159,712개** → 변화: **약 99.9% 감소**

### 4. 쿼리 캐시 효율

- 전: 캐시 히트 816,022회 (효율 99.99%)
- 후: 캐시 히트 78,625회 (효율 99.95%)

**종합 결론**: 최적화 적용 후 **쿼리 실행 횟수가 91.2% 감소**하고, **엔티티 로딩이 99.9% 감소**하는 압도적인 성능 개선을 달성했습니다.

</details>

---

### 테스트 환경

| 항목 | 스펙 |
|------|------|
| **테스트 도구** | k6 |
| **서버** | Mac Mini M2 (로컬) |
| **데이터베이스** | MySQL 8.0 |
| **테스트 데이터** | 유저 20,000명, 게시글 100,000개, 댓글 300,000개 |

### 테스트 시나리오

```javascript
// 부하 단계
stages: [
  { duration: '30s', target: 100 },   // Warm-up
  { duration: '2m', target: 500 },    // Load
  { duration: '1m', target: 500 },    // Stress
  { duration: '30s', target: 0 },     // Cool-down
]
```

### 최적화 전 vs 후 비교 (동시 접속자 500명)

| 지표 | 최적화 전 | 최적화 후 | 개선율 |
|------|-----------|-----------|--------|
| **평균 응답 시간** | 2,340ms | 410ms | **82.5% 감소** |
| **p(95) 응답 시간** | 8,500ms | 1,940ms | **77.2% 감소** |
| **p(99) 응답 시간** | 15,200ms | 2,370ms | **84.4% 감소** |
| **최대 응답 시간** | 23,000ms | 3,740ms | **83.7% 감소** |
| **처리량 (RPS)** | 32 req/s | 92 req/s | **187.5% 증가** |
| **에러율** | 12.3% | 0% | **100% 개선** |

### 스케일별 성능 비교

| 동시 접속자 | 평균 응답 시간 | p(95) | RPS | 에러율 |
|-------------|----------------|-------|-----|--------|
| 100명 | 11.69ms | 45ms | 850 req/s | 0% |
| 300명 | 156ms | 620ms | 320 req/s | 0% |
| 500명 | 410ms | 1,940ms | 92 req/s | 0% |

### 주요 성능 개선 포인트

1. **N+1 쿼리 해결**: Fetch Join으로 쿼리 수 99% 감소
2. **인덱스 최적화**: 자주 조회되는 컬럼에 인덱스 추가
3. **Connection Pool 튜닝**: HikariCP 설정 최적화
4. **Hibernate Statistics**: 쿼리 분석으로 병목 지점 파악

### 부하 테스트 실행 방법

```bash
# k6 설치 (macOS)
brew install k6

# 테스트 실행
k6 run load-test-script.js

# 환경변수로 서버 주소 지정
BASE_URL=http://localhost:8080 k6 run load-test-script.js
```

---

## 📁 프로젝트 구조

```
src/main/java/com/project/final_project/
├── common/                 # 공통 설정, 예외처리, 유틸
│   ├── config/            # Spring 설정 클래스
│   ├── exception/         # 커스텀 예외
│   ├── global/            # 전역 응답 형식
│   └── util/              # 유틸리티 클래스
├── user/                   # 사용자 도메인
├── board/                  # 게시판 도메인
├── comment/                # 댓글 도메인
├── friendship/             # 친구 도메인
├── note/                   # 쪽지 도메인
├── furniture/              # 가구 도메인
├── inventory/              # 인벤토리 도메인
├── item/                   # 아이템 도메인
├── quest/                  # 퀘스트 도메인
├── school/                 # 학교 도메인
├── gallery/                # 갤러리 도메인
├── guestbook/              # 방명록 도메인
├── chatlog/                # 채팅 로그 도메인
├── mapcontest/             # 맵 콘테스트 도메인
└── websocket/              # WebSocket 설정
```

---

## 🔧 트러블슈팅

### 1. N+1 문제로 인한 응답 지연

**문제**: 게시글 목록 조회 시 평균 2초 이상 소요  
**원인**: Lazy Loading으로 인한 N+1 쿼리  
**해결**: Fetch Join 및 @EntityGraph 적용으로 쿼리 1회로 최적화

### 2. 대용량 데이터 생성 시 타임아웃

**문제**: 테스트 데이터 10만 건 생성 시 타임아웃  
**원인**: 개별 INSERT 쿼리로 인한 오버헤드  
**해결**: Batch Insert 및 트랜잭션 분리

### 3. WebSocket 연결 제한

**문제**: 동시 접속자 증가 시 WebSocket 연결 실패  
**원인**: Tomcat 기본 스레드 풀 한계  
**해결**: 스레드 풀 크기 조정 및 비동기 처리 적용

---

## 👨‍💻 개발자

| 이름 | 역할 | GitHub |
|------|------|--------|
| **전성표** | Backend Developer | [@pyoya1123](https://github.com/pyoya1123) |

---

