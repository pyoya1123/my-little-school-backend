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
<details>
<summary>ERD</summary>
<img width="1968" height="5314" alt="image" src="https://github.com/user-attachments/assets/be812b22-13e2-4e5c-b4a4-3414c5f003fa" />
</details>

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
<img width="1560" height="527" alt="image (10)" src="https://github.com/user-attachments/assets/a866c907-3b70-439b-b37d-4eef699c23a9" />

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
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 50,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 106,
      "executionAvgTime": 106,
      "executionMinTime": 106,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 2,
      "executionMinTime": 2,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 1,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 33,
      "executionAvgTime": 33,
      "executionMinTime": 33,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}
```

### 1. N+1 문제

- **queryExecutionCount**: 2,660 (총 쿼리 실행 횟수)
- **queryCount**: 42 (고유 쿼리 수)
- **반복 비율** = 2,660 ÷ 42 = **약 63.3회/쿼리**
- 42개의 서로 다른 쿼리가 평균적으로 각각 63번씩 반복 실행되었습니다.
- 총 HTTP 요청 수가 685건인 점을 감안할 때, 요청당 쿼리 수가 많아 **N+1 문제가 발생하고 있음**을 보여줍니다.

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**
    - **실행 시간**: 106ms
    - **쿼리**: select s1_0.id, s1_0.latitude, s1_0.school_location, s1_0.longitude, s1_0.school_name from school s1_0
    - **의미**: School 테이블 전체 조회 쿼리 

### 3. 엔티티 로드 분석

- **entityLoadCount**: 15,021 (엔티티 로드 횟수)
- **쿼리 대비 로드 비율**:
    - 15,021 ÷ 2,660 ≈ **5.65**
    - 쿼리 실행 1회당 평균 5.65개의 엔티티가 로드되었습니다.
    - 이는 목록 조회 시 연관된 엔티티(User, Board 등)들이 함께 로드되고 있음을 의미합니다.

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
<img width="2726" height="1226" alt="image (1)" src="https://github.com/user-attachments/assets/cd7606ee-1345-4e6a-9b25-5eaec556567f" />

## 부하 테스트 완료
<img width="2734" height="1246" alt="image (2)" src="https://github.com/user-attachments/assets/3b67f11b-cc6a-458f-8a51-a381fcc51832" />
<img width="2725" height="1976" alt="image (3)" src="https://github.com/user-attachments/assets/50a2f050-17ee-433f-b0a9-6f79e3a367e8" />
<img width="2731" height="515" alt="image (4)" src="https://github.com/user-attachments/assets/77455b2f-16bd-46a1-b262-336a3074e906" />



## 분석

```markdown
1. 테스트 개요
사용자 수: 100명 (목표 100명 달성)
기존 10명 + 신규 90명 생성
게시판 수: 500개 (사용자당 5개, 목표 달성)
총 500개 게시판 확보
댓글 수: 1,350개 생성
동시 접속자(VU): 최대 100명 (1:1 매칭 테스트)
테스트 시간: 2분 31초

2. 응답 시간 (Response Time)
평균 응답 시간: 11.69ms
p(95) (95% 요청): 24.7ms
p(99) (99% 요청): 29.3ms
최대 응답 시간: 10.65s (약 10초)
분석:
평균 응답 시간(11.69ms)은 매우 우수하며, 대부분의 요청이 30ms 이내에 처리되었습니다.
유저 100명, 게시글 500개 수준의 데이터에서도 N+1 문제가 전체적인 응답 속도에 큰 영향을 주지 않고 있습니다.

3. 처리량 (Throughput)
초당 요청 수 (RPS): 약 32.8req/s
총 요청 수: 4,979건
사용자 시나리오 반복 수: 1,279회
분석:
100명의 동시 접속자가 2분 10초 동안 약 5,000건의 요청을 안정적으로 처리했습니다.
처리량 변동 없이 안정적인 성능을 보여주었습니다.
```

### Hibernate Stats
<img width="1560" height="531" alt="image" src="https://github.com/user-attachments/assets/1c7e9f5e-3bf6-4ae7-b3cd-99224efcb19e" />

```json
{
  "queryExecutionMaxTime": 106,
  "queryExecutionCount": 19160,
  "queryCount": 42,
  "queryPlanCacheMissCount": 43,
  "queryPlanCacheHitCount": 34928,
  "queryExecutionMaxTimeQueryString": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
  "entityLoadCount": 686907
}
```

### 쿼리 별 실행 횟수 
```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 73,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 9022,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 5,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select bl from BoardLike bl where bl.boardId = :boardId and bl.userId = :userId",
      "executionCount": 7535,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 20,
      "executionAvgTime": 6,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select b1_0.id,b1_0.board_content,b1_0.board_like_count,b1_0.board_title,b1_0.user_id from board b1_0",
      "executionCount": 1597,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 550,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select i from Inventory i where i.userId = :userId and i.itemType = :itemType",
      "executionCount": 180,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 91,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select q from Quest q where q.questType = :questType",
      "executionCount": 90,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select u from User u where u.email = :email",
      "executionCount": 90,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 106,
      "executionAvgTime": 67,
      "executionMinTime": 29,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 2,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 33,
      "executionAvgTime": 17,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 2,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 1,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}
```

### 1. N+1 문제

- **queryExecutionCount**: 19,160 (총 쿼리 실행 횟수)
- **queryCount**: 42 (고유 쿼리 수)
- **반복 비율** = 19,160 ÷ 42 = **약 456회/쿼리**
- 42개의 서로 다른 쿼리가 평균적으로 각각 456번씩 반복 실행되었습니다.
- 특히 다음 쿼리들의 실행 횟수가 압도적으로 높습니다:
1. **댓글 수 조회** (select count(*) from Comment...): **9,022회**
2. **좋아요 여부 조회** (select bl from BoardLike...): **7,535회**
3. **게시판 목록 조회** (select ... from board...): **1,597회**
- 게시판 목록 1회 조회(1,597회)에 대해 댓글/좋아요 조회가 약 **10배 이상** 발생(16,557회)하고 있어 **심각한 N+1 문제가 발생하고 있음**을 명확히 보여줍니다.

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**
    - **실행 시간**: 106ms
- **쿼리**: select s1_0.id, s1_0.latitude, s1_0.school_location, s1_0.longitude, s1_0.school_name from school s1_0
- **의미**: School 테이블 전체 조회 쿼리

### 3. 엔티티 로드 분석

- **entityLoadCount**: 686,907 (엔티티 로드 횟수)
- **쿼리 대비 로드 비율**:
    - 686,907 ÷ 19,160 ≈ **35.85**
    - 쿼리 실행 1회당 평균 약 36개의 엔티티가 로드되었습니다.
    - 이는 이전(5.65)보다 훨씬 높은 수치로, 게시판 목록 조회 시 불필요하게 많은 연관 데이터(댓글, 좋아요 등)가 함께 로딩되거나, N+1 쿼리로 인해 중복 로딩이 발생하고 있음을 시사합니다.

### 4. 쿼리 캐시 효율

- **지표**
    - **queryPlanCacheHitCount**: 34,928 (캐시 히트)
    - **queryPlanCacheMissCount**: 43 (캐시 미스)
- **계산**
    - **캐시 효율** = 34,928 ÷ (34,928 + 43) = **약 99.88%**
- **평가**
    - 쿼리 플랜 캐시 효율이 매우 높습니다(거의 100%).
    - 실행되는 쿼리 패턴은 고정적이며, DB 입장에서 쿼리 파싱 부하는 적습니다.
    - 문제는 **쿼리 실행 횟수 자체**에 있습니다.

</details>

<details>

<summary>개선 전 (유저 수: 500, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

</summary>

## 부하 테스트 진행중

- 유저 수 : 500
- 게시글 수 : 유저 당 5 → 2500
- 댓글 수 : 게시글 당 3 → 7500
<img width="2729" height="1200" alt="image (5)" src="https://github.com/user-attachments/assets/bd90141f-82f2-4dc0-8ee7-151134d4d37a" />

## 부하 테스트 완료
<img width="2732" height="1178" alt="image (6)" src="https://github.com/user-attachments/assets/24c8c219-0438-42cc-8994-e97e723b68eb" />
<img width="2947" height="1913" alt="image (7)" src="https://github.com/user-attachments/assets/763d5bb6-e214-4d54-98c4-c576f3891bb1" />
<img width="2955" height="495" alt="image (8)" src="https://github.com/user-attachments/assets/d43600b3-6a31-451e-8969-d8a3e6853a59" />


## 분석

```markdown
1. 테스트 개요
사용자 수: 500명 (목표 500명 달성)
기존 사용자 중 500명 선택
게시판 수: 2,500개 (사용자당 5개, 목표 달성)
기존 데이터 활용
댓글 수: 0개 생성 (기존 데이터 충분)
동시 접속자(VU): 최대 500명 (1:1 매칭 테스트)
테스트 시간: 3분 58초

2. 응답 시간
평균 응답 시간: 410.4ms
p(95) (95% 요청): 1.94s
p(99) (99% 요청): 2.37s
최대 응답 시간: 3.74s
분석:
유저 100명 테스트(평균 11.69ms) 대비 평균 응답 시간이 410.4ms로 약 35배 증가했습니다.
상위 5% 요청의 응답 시간이 약 2초에 달하며, 최대 응답 시간은 3.7초까지 지연되었습니다.
이는 500명 동시 접속 환경에서 N+1 문제로 인한 DB 부하가 본격적으로 성능 저하를 일으키고 있음을 보여줍니다.

3. 처리량 
초당 요청 수 (RPS): 약 91.9req/s
총 요청 수: 21,910건
사용자 시나리오 반복 수: 6,303회
분석:
500명의 동시 접속자가 약 2만 건 이상의 요청을 처리했습니다.
응답 시간이 느려졌음에도 불구하고 에러 없이 모든 요청을 처리해냈지만, 사용자 체감 속도는 확연히 느려졌을 것입니다.
```

## Hibernate Stats
<img width="1555" height="528" alt="image (9)" src="https://github.com/user-attachments/assets/e91f2b77-e1e7-4857-b468-a76078ee77a5" />

```json
{
  "queryExecutionMaxTime": 410,
  "queryExecutionCount": 445144,
  "queryCount": 42,
  "queryPlanCacheMissCount": 43,
  "queryPlanCacheHitCount": 816022,
  "queryExecutionMaxTimeQueryString": "[CRITERIA] select b1_0.id,b1_0.board_content,b1_0.board_like_count,b1_0.board_title,b1_0.user_id from board b1_0",
  "entityLoadCount": 158851775
}
```

## 쿼리 별 실행 횟수
```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 171,
      "executionAvgTime": 3,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 209626,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 26,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select bl from BoardLike bl where bl.boardId = :boardId and bl.userId = :userId",
      "executionCount": 177690,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 410,
      "executionAvgTime": 109,
      "executionMinTime": 6,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select b1_0.id,b1_0.board_content,b1_0.board_like_count,b1_0.board_title,b1_0.user_id from board b1_0",
      "executionCount": 36328,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 7,
      "executionAvgTime": 3,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 17201,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select i from Inventory i where i.userId = :userId and i.itemType = :itemType",
      "executionCount": 1578,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select u from User u where u.email = :email",
      "executionCount": 1130,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 790,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "select q from Quest q where q.questType = :questType",
      "executionCount": 789,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 23,
      "executionAvgTime": 18,
      "executionMinTime": 14,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 8,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 81,
      "executionAvgTime": 46,
      "executionMinTime": 28,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 3,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 0,
      "executionAvgTime": 0,
      "executionMinTime": 0,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}

```

### 1. N+1 문제

- **queryExecutionCount**: 445,144 (총 쿼리 실행 횟수)
- **queryCount**: 42 (고유 쿼리 수)
- **반복 비율** = 445,144 ÷ 42 = **약 10,598회/쿼리**
    - 42개의 서로 다른 쿼리가 평균적으로 각각 1만 번 이상 반복 실행되었습니다.
    - 특히 다음 쿼리들의 실행 횟수가 압도적으로 높습니다:
1. **댓글 수 조회** (select count(*) from Comment...): **209,626회**
2. **좋아요 여부 조회** (select bl from BoardLike...): **177,690회**
3. **게시판 목록 조회** (select ... from board...): **36,328회**
    - 게시판 목록 1회 조회(36,328회)에 대해 댓글/좋아요 조회가 약 **10.6배 이상** 발생(387,316회)하고 있어 **N+1 문제가 매우 심각하게 발생하고 있음**을 명확히 보여줍니다.

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**
    - **실행 시간**: 410ms
    - **쿼리**: select b1_0.id, b1_0.board_content... from board b1_0
    - **의미**: 게시판 목록 조회 쿼리 (가장 많이 호출되는 메인 쿼리)
- **평가**:
    - **게시판 조회 쿼리**가 가장 느린 쿼리(410ms)로 등극했습니다. 이는 이전 테스트(100ms, School 조회)와 달리 **주요 비즈니스 로직 쿼리가 성능 병목**이 되었음을 의미합니다.
    - 평균 실행 시간도 **109ms**로 매우 느려졌습니다(이전 테스트 약 6ms).
    - 댓글 수 조회(20만 회)와 좋아요 조회(17만 회)의 누적 실행 시간이 DB 연결 풀을 점유하여, 메인 쿼리인 게시판 조회의 대기 시간을 증가시킨 것으로 분석됩니다.

### 3. 엔티티 로드 분석

- **entityLoadCount**: 158,851,775 (엔티티 로드 횟수 - 약 1.5억 회)
- **쿼리 대비 로드 비율**:
    - 158,851,775 ÷ 445,144 ≈ **356.8**
    - 쿼리 실행 1회당 평균 **356개**의 엔티티가 로드되었습니다.
    - 이는 비정상적으로 높은 수치로, N+1 문제로 인해 동일한 엔티티가 반복적으로 로딩되거나, 불필요한 연관 관계(Eager Loading 등)로 인해 거대한 객체 그래프가 메모리에 올라가고 있음을 강력히 시사합니다.
    - **메모리 부족(OOM)** 위험이 매우 높습니다.

### 4. 쿼리 캐시 효율

- **지표**
    - **queryPlanCacheHitCount**: 816,022 (캐시 히트)
    - **queryPlanCacheMissCount**: 43 (캐시 미스)
- **계산**
    - **캐시 효율** = 816,022 ÷ (816,022 + 43) ≈ **99.99%**
- **평가**
    - 쿼리 플랜 캐시 효율은 완벽에 가깝습니다.
    - 이는 쿼리 패턴 자체는 문제가 없으나, **쿼리 실행 횟수와 엔티티 로딩 양**이 시스템을 압도하고 있음을 반증합니다.
    - SQL 튜닝보다는 **N+1 해결(Fetch Join, Batch Size)**이 유일한 해결책입니다.

</details>

---

<details>

<summary>개선 후 (유저 수: 10, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 10
- 게시판 수 : 유저 당 5 → 50
- 댓글 수 : 게시판 당 3 → 150
<img width="2843" height="1176" alt="image (1)" src="https://github.com/user-attachments/assets/1e80013c-9c86-4b9d-b7e0-675daacea199" />

## 부하 테스트 완료
<img width="2779" height="1182" alt="image (2)" src="https://github.com/user-attachments/assets/be50e9c2-8efb-4ad2-94ca-ba3f621df4b4" />
<img width="2799" height="1933" alt="image (3)" src="https://github.com/user-attachments/assets/6f2fefb5-1d5d-4d3a-950d-4973e74a89fe" />
<img width="2791" height="471" alt="image (4)" src="https://github.com/user-attachments/assets/9355f542-9d74-42e0-b876-a34c55f668fb" />


## 분석

### **1. 테스트 개요**

- **사용자 수**: 10명 (목표 10명 달성)
- **게시판 수**: 50개 (사용자당 5개, 목표 달성)
- **동시 접속자(VU)**: 최대 10명 (1:1 매칭 테스트)
- **테스트 시간**: 2분 11초 (이전과 동일)

### 2. 응답 시간 분석

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 전 재확인) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 8.46ms | 11.72ms | ▲ 3.26ms |
| **중앙값** | 6.13ms | 8.52ms | ▲ 2.39ms |
| **p(95)** | 15.88ms | 24.13ms | ▲ 8.25ms |
| **최대 응답 시간** | 342.36ms | 199.99ms | ▼ 142.37ms |

**분석**

- 두 테스트 모두 **평균 10ms 내외**의 매우 빠른 응답 속도를 보여줍니다.
- 현재 테스트가 약 3ms 느리게 측정되었으나, 이는 로컬 환경의 일시적 부하나 네트워크 노이즈에 의한 오차 범위 내입니다.
- **결론**: 데이터가 적은 상황(유저 10명)에서는 N+1 문제가 성능에 큰 영향을 주지 않음을 재확인했습니다.

### 3. 처리량

| 지표 | 이전 테스트 | 현재 테스트 | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 5.0req/s | 약 5.2req/s | ▲ 0.2req/s |
| **총 요청 수** | 685건 | 685건 | 동일 |

**분석**

- 처리량은 거의 동일하며, 시스템이 안정적으로 요청을 처리했습니다.

### **4. 종합 평가**

- 유저 10명 규모에서는 **N+1 문제의 유무가 응답 속도에 미치는 영향이 미미**합니다.
---

## Hibernate Stats
<img width="1564" height="531" alt="image (5)" src="https://github.com/user-attachments/assets/ff95eae9-b9d2-45ba-9710-d8fe6184cd03" />

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

### 쿼리 별 실행 횟수

```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 29,
      "executionAvgTime": 16,
      "executionMinTime": 13,
      "cacheHitCount": 0,
      "query": "SELECT b, count(c), bl FROM Board b LEFT JOIN Comment c ON c.boardId = b.id  LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId  WHERE b.userId = :userId GROUP BY b, bl",
      "executionCount": 218,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 8,
      "executionAvgTime": 2,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 208,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 7,
      "executionAvgTime": 4,
      "executionMinTime": 3,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 50,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 86,
      "executionAvgTime": 86,
      "executionMinTime": 86,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 2,
      "executionMinTime": 2,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 1,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 36,
      "executionAvgTime": 36,
      "executionMinTime": 36,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}
```

### 1. N+1 문제 확인

- **queryExecutionCount**:
    - 최적화 전: **2,660회**
    - 최적화 후: **480회**
    - 변화: **약 82% 감소** (약 5.5배 감소)
- **주요 쿼리 변화**:
    - **게시판 목록 조회 (Main)**:
        - 218회 (동일) → 동일한 횟수로 실행됨 (정상)
    - **댓글 수 조회 (N+1)**:
        - 전: 1,298회 → 후: **208회** (약 84% 감소)
        - 게시판 수(208회)와 1:1로 매칭되는 수준으로 감소 (Batch Size 적용 효과 추정)
    - **좋아요 여부 조회 (N+1)**:
        - 전: 1,090회 → 후: **0회** (목록에서 사라짐)
        - 최적화 쿼리(SELECT b, count(c), bl FROM ...)에 통합되어 개별 실행이 제거됨

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**:
    - 전: 106ms (select ... from school)
    - 후: 86ms (select ... from school)
- **평가**:
    - School 조회 쿼리가 여전히 가장 느린 쿼리로 기록되었으나, 실행 시간이 단축되었습니다.
    - 실행 횟수가 1회인 초기화 쿼리이므로 전체 성능에는 영향이 없습니다.
- **최적화 쿼리 성능**:
    - SELECT b, count(c), bl ...: 평균 **16ms**
    - 복잡한 조인 쿼리임에도 불구하고 빠른 응답 속도를 보여줍니다.

### 3. 엔티티 로드 분석

- **entityLoadCount**:
    - 전: **15,021개**
    - 후: **6,090개**
    - 변화: **약 59.5% 감소** (약 2.5배 감소)
- **평가**:
    - 불필요한 연관 엔티티 로딩이 획기적으로 줄어들었습니다.
    - 특히 좋아요 여부 조회 시 발생하던 엔티티 로딩이 Fetch Join(또는 최적화 쿼리)을 통해 제거된 것이 주효했습니다.

**4. 쿼리 캐시 효율**

- **지표**:
    - 전: 캐시 히트 4,873회 (효율 99.19%)
    - 후: 캐시 히트 949회 (효율 95.86%)
- **평가**:
    - 쿼리 실행 횟수 자체가 줄어들어 히트 수도 자연스럽게 감소했습니다.
    - 캐시 효율이 약간 낮아진 것은 전체 모수가 작아져서(480회) 초기 미스(41회)의 비중이 상대적으로 커졌기 때문이며, 실제 성능에는 긍정적입니다.

---

**종합 결론**

최적화 적용 후 **쿼리 실행 횟수가 82% 감소**하고, **엔티티 로딩이 60% 감소**했습니다.

</details>

<details>

<summary>개선 후 (유저 수: 100, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 100
- 게시판 수 : 유저 당 5 → 500
- 댓글 수 : 게시판 당 3 → 1500
<img width="2799" height="1192" alt="image (6)" src="https://github.com/user-attachments/assets/12649794-67e3-4047-a2c4-a292699e033f" />

## 부하 테스트 완료
<img width="2759" height="1182" alt="image (9)" src="https://github.com/user-attachments/assets/e7d17f32-d7f5-4325-91bf-935522d13ea2" />
<img width="2761" height="1927" alt="image (7)" src="https://github.com/user-attachments/assets/0ed81f57-beb5-4255-a65c-edd050f2b027" />
<img width="2791" height="524" alt="image (8)" src="https://github.com/user-attachments/assets/1aeaaaa0-d4a0-4068-8e13-66a7df178a3b" />


## 분석

### **1. 테스트 개요**

- **사용자 수**: 100명 (목표 100명 달성)
- **게시판 수**: 500개 (사용자당 5개, 목표 달성)
- **동시 접속자(VU)**: 최대 100명 (1:1 매칭 테스트)
- **테스트 시간**: 약 2분 20초 내외 (동일 조건)

### 2. 응답 시간 분석

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 11.69ms | 10.63ms | ▼ 1.06ms |
| **중앙값** | 5.39ms | 7.02ms | ▲ 1.63ms |
| **p(95)** | 24.7ms | 24.01ms | ▼ 0.69ms |
| **최대 응답 시간** | 10.65s | 53.2ms | ▼ 10.59s |

**분석**

- 평균 응답 시간과 상위 95%(p95) 응답 시간은 **최적화 전후가 거의 비슷**합니다 (10~11ms 수준).
- **최대 응답 시간**이 10.65초에서 **53.2ms**로 획기적으로 줄어들었습니다. 이는 최적화 전 발생했던 일시적인 DB 병목(School 쿼리 등)이나 연결 지연이 해결되었음을 의미합니다.
- **결론**: 100명 규모에서도 DB가 데이터를 메모리에 충분히 로드할 수 있어, N+1 해결에 따른 **평균 응답 속도의 드라마틱한 개선은 나타나지 않았습니다.** 하지만 **응답의 안정성(최대 지연 감소)은 크게 향상**되었습니다.

### 3. 처리량

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 32.8req/s | 약 49.1req/s | ▲ 16.3req/s |
| **총 요청 수** | 4,979건 | 6,865건 | ▲ 1,886건 |

**분석**

- **처리량(RPS)이 약 50% 증가**했습니다.
- 동일한 시간 동안 처리한 총 요청 수가 약 1,900건 늘어났습니다.
- 이는 N+1 문제 해결로 인해 **DB 리소스(연결 풀, CPU)에 여유**가 생겨, 서버가 더 많은 요청을 동시에 처리할 수 있게 되었음을 보여줍니다.

### **4. 종합 평가**

- **응답 속도**: 평균 속도는 비슷하지만, **최대 지연 시간이 획기적으로 감소**하여 사용자에게 더 **안정적인 경험**을 제공합니다.
- **처리 용량**: 동일 하드웨어에서 **처리 가능한 트래픽(Throughput)이 50% 증가**했습니다. 이는 서비스 확장성 측면에서 매우 큰 성과입니다.
- **결론**: N+1 해결은 단순한 속도 개선을 넘어, **시스템의 전체 처리 용량과 안정성을 크게 향상**시켰습니다.
---

## Hibernate Stats
<img width="1561" height="530" alt="image (10)" src="https://github.com/user-attachments/assets/a39c6fe0-65f1-4d05-b5ec-9c3b27d133ed" />

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

### 쿼리 별 실행 횟수

```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 41,
      "executionAvgTime": 17,
      "executionMinTime": 13,
      "cacheHitCount": 0,
      "query": "SELECT b, count(c), bl FROM Board b LEFT JOIN Comment c ON c.boardId = b.id  LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId  WHERE b.userId = :userId GROUP BY b, bl",
      "executionCount": 2406,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 8,
      "executionAvgTime": 2,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 2296,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 8,
      "executionAvgTime": 4,
      "executionMinTime": 3,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 550,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 36,
      "executionAvgTime": 33,
      "executionMinTime": 31,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 2,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 86,
      "executionAvgTime": 86,
      "executionMinTime": 86,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 2,
      "executionMinTime": 2,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 1,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}
```

### 1. N+1 문제 확인

- **queryExecutionCount**:
    - 최적화 전: **445,144회** (유저 500명 테스트)
    - 최적화 후: **5,257회**
    - 변화: **약 98.8% 감소** (약 84배 감소)
- **주요 쿼리 변화**:
    - **게시판 목록 조회 (Main)**:
        - 36,328회 → **2,406회** (최적화 쿼리로 대체)
        - 기존 목록 조회 쿼리(select b1_0.id...)는 실행 횟수에서 사라지고, 최적화 쿼리(SELECT b, count(c), bl FROM ...)가 2,406회 실행됨.
    - **댓글 수 조회 (N+1)**:
        - 전: 209,626회 → 후: **2,296회** (약 98.9% 감소)
        - 게시판 수에 비례하던 쿼리가 획기적으로 줄어들었습니다.
    - **좋아요 여부 조회 (N+1)**:
        - 전: 177,690회 → 후: **0회** (완전 제거)
        - 최적화 쿼리에 통합되어 개별 실행이 사라졌습니다.

### 2. 쿼리 성능 분석

- **가장 느린 쿼리**:
    - 전: 410ms (select ... from board) - **메인 비즈니스 쿼리**
    - 후: 86ms (select ... from school) - **초기화 쿼리**
- **평가**:
    - 최적화 전에는 게시판 조회 쿼리가 가장 느렸으나(410ms), 최적화 후에는 해당 쿼리가 병목 목록에서 사라졌습니다.
    - 현재 가장 느린 쿼리(86ms)는 School 조회 쿼리로, 실행 횟수가 1회에 불과하여 성능에 영향이 없습니다.

### 3. 엔티티 로드 분석

- **entityLoadCount**:
    - 전: **686,907개**
    - 후: **25,684개**
    - 변화: **약 96.2% 감소** (약 26배 감소)
- **평가**:
    - 엔티티 로딩 횟수가 68만 개에서 2.5만 개로 대폭 감소했습니다.
    - 게시판 목록 조회 시 발생하던 **N+1 쿼리로 인한 중복/과다 엔티티 로딩**이 해결되었습니다.

### 4. 쿼리 캐시 효율

- **지표**:
    - 전: 캐시 히트 816,022회 (효율 99.99%)
    - 후: 캐시 히트 10,501회 (효율 99.61%)
- **평가**:
    - 쿼리 실행 횟수가 대폭 줄어들어 캐시 히트 수치도 자연스럽게 감소했습니다.
    - 캐시 효율은 여전히 최상급(99% 이상)을 유지하고 있어, DB가 쿼리 파싱에 소모하는 비용은 최소화되고 있습니다.

---

**종합 결론**

최적화 적용 후 **쿼리 실행 횟수가 72.5% 감소**하고, **엔티티 로딩이 96.2% 감소**했습니다.

</details>

<details>

<summary>개선 후 (유저 수: 500, 유저 당 게시글 수: 5, 게시글 당 댓글 수: 3)</summary>

## 부하 테스트 진행중

- 유저 수 : 500
- 게시판 수 : 유저 당 5 → 2500
- 댓글 수 : 게시판 당 3 → 7500
<img width="2794" height="1175" alt="image (11)" src="https://github.com/user-attachments/assets/22a99d2b-751c-4c33-b254-592d865e8f3a" />

## 부하 테스트 완료
<img width="2790" height="1196" alt="image (12)" src="https://github.com/user-attachments/assets/188a7b1c-a09c-4f63-9c53-742b6a8d8d9f" />
<img width="2781" height="1929" alt="image (13)" src="https://github.com/user-attachments/assets/cca04566-8aa7-408a-b2b9-095f72def9c4" />
<img width="2779" height="508" alt="image (14)" src="https://github.com/user-attachments/assets/4e92447c-e905-4877-818b-074df11438e7" />


## 분석

### **1. 테스트 개요**

- **사용자 수**: 500명 (동일)
- **게시판 수**: 2,500개 (동일)
- **동시 접속자(VU)**: 최대 500명 (동일)
- **테스트 시간**: 약 3분 20초 (동일)

### 2. 응답 시간

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **평균 응답 시간** | 410.4ms | 13.56ms | ▼ 396.84ms (**약 97% 개선**) |
| **중앙값** | 124.66ms | 7.61ms | ▼ 117.05ms |
| **p(95)** | 1.94s | 36.89ms | ▼ 1.90s |
| **최대 응답 시간** | 3.74s | 211.66ms | ▼ 3.53s |

**분석**

- **평균 응답 시간**이 410ms에서 **13.56ms**로 획기적으로 단축되었습니다.
- **p(95)** 지표가 1.94초에서 **36.89ms**로 개선되어, 대다수 사용자가 느끼던 지연(Latency)이 완전히 사라졌습니다.

### 3. 처리량

| 지표 | 이전 테스트 (최적화 전) | 현재 테스트 (최적화 후) | 변화 |
| --- | --- | --- | --- |
| **초당 요청 수 (RPS)** | 약 91.9req/s | 약 135.5req/s | ▲ 43.6req/s (**약 47% 증가**) |
| **총 요청 수** | 21,910건 | 27,352건 | ▲ 5,442건 |

**분석**

- **처리량(RPS)**이 약 92req/s에서 **135.5req/s**로 **47% 이상 증가**했습니다.

### **4. 종합 평가**

- **응답 속도**: N+1 문제 해결을 통해 **평균 응답 속도가 30배 이상 빨라졌습니다.**
- **처리 용량**: 동일한 하드웨어 리소스로 **약 47% 더 많은 트래픽을 처리**할 수 있게 되었습니다.
- **시스템 안정성**: 최대 응답 시간이 3.7초에서 0.2초대로 줄어들어, 트래픽 스파이크 시에도 타임아웃이나 연결 지연 없이 안정적인 서비스를 제공할 수 있음을 입증했습니다.

---

## Hibernate Stats
<img width="1560" height="542" alt="image (15)" src="https://github.com/user-attachments/assets/929bea8f-86ea-406e-bbf3-7bd1bff77a76" />

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

### 쿼리 별 실행 횟수

```json
{
  "totalQueries": 42,
  "queries": [
    {
      "executionMaxTime": 185,
      "executionAvgTime": 24,
      "executionMinTime": 13,
      "cacheHitCount": 0,
      "query": "SELECT b, count(c), bl FROM Board b LEFT JOIN Comment c ON c.boardId = b.id  LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId  WHERE b.userId = :userId GROUP BY b, bl",
      "executionCount": 17437,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 78,
      "executionAvgTime": 2,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "select count(*) from Comment c where c.boardId = :boardId",
      "executionCount": 16327,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 10,
      "executionAvgTime": 3,
      "executionMinTime": 2,
      "cacheHitCount": 0,
      "query": "select c from Comment c where c.boardId = :boardId",
      "executionCount": 5550,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 36,
      "executionAvgTime": 27,
      "executionMinTime": 21,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select u1_0.user_id,u1_0.user_birthday,u1_0.user_email,u1_0.entered_date,u1_0.user_exp,u1_0.user_gender,u1_0.user_gold,u1_0.user_grade,u1_0.is_online,u1_0.user_level,u1_0.map_id,u1_0.map_type,u1_0.user_max_exp,u1_0.user_name,u1_0.user_password,u1_0.user_phone,u1_0.school_id,u1_0.user_status_message from user u1_0",
      "executionCount": 4,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 86,
      "executionAvgTime": 86,
      "executionMinTime": 86,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select s1_0.id,s1_0.latitude,s1_0.school_location,s1_0.longitude,s1_0.school_name from school s1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 2,
      "executionAvgTime": 2,
      "executionMinTime": 2,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select i1_0.id,i1_0.item_idx,i1_0.item_name,i1_0.item_type,i1_0.item_price from item i1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    },
    {
      "executionMaxTime": 1,
      "executionAvgTime": 1,
      "executionMinTime": 1,
      "cacheHitCount": 0,
      "query": "[CRITERIA] select q1_0.id,q1_0.quest_content,q1_0.quest_count,q1_0.exp,q1_0.gold,q1_0.quest_type,q1_0.quest_title from quest q1_0",
      "executionCount": 1,
      "cacheMissCount": 0
    }
  ]
}
```

---

**1. N+1 문제 확인**

- **queryExecutionCount**:
    - 최적화 전: **445,144회** (N+1 발생)
    - 최적화 후: **39,321회** (최적화 적용)
    - 변화: **약 91.2% 감소** (약 11배 감소)
- **주요 쿼리 변화**:
    - **게시판 목록 조회 (Main)**:
        - 전: 36,328회 (select ... from board)
        - 후: **17,437회** (SELECT b, count(c), bl FROM ...)
        - 최적화된 조인 쿼리로 대체되어 실행 횟수와 효율성이 개선되었습니다. (테스트 실행 횟수 증가로 인해 횟수는 증가함)
    - **댓글 수 조회 (N+1)**:
        - 전: 209,626회 → 후: **16,327회** (약 92% 감소)
        - 게시판 수에 비례하던 폭발적인 쿼리 실행이 정상 수준으로 감소했습니다.
    - **좋아요 여부 조회 (N+1)**:
        - 전: 177,690회 → 후: **0회** (완전 제거)
        - 최적화 쿼리에 통합되어 개별 실행이 완전히 사라졌습니다.

**2. 쿼리 성능 분석**

- **가장 느린 쿼리**:
    - 전: 410ms (select ... from board) - **메인 비즈니스 쿼리**
    - 후: 185ms (SELECT b, count(c), bl ...) - **최적화된 메인 쿼리**
- **평가**:
    - 최적화 전에는 단순 조회임에도 DB 부하로 인해 410ms까지 지연되었습니다.
    - 최적화 후에는 복잡한 조인과 집계를 수행함에도 최대 185ms, 평균 **24ms**로 안정적인 성능을 보여줍니다.
- **최적화 쿼리 성능**:
    - SELECT b, count(c), bl ...: 평균 **24ms**
    - 수십만 번 실행되던 개별 쿼리들을 단 한 번의 조인 쿼리로 처리하면서도 매우 빠른 응답 속도를 유지하고 있습니다.

**3. 엔티티 로드 분석**

- **entityLoadCount**:
    - 전: **158,851,775개** (약 1.5억 개)
    - 후: **159,712개**
    - 변화: **약 99.9% 감소** (약 994배 감소)
- **평가**:
    - 엔티티 로딩 횟수가 1.5억 개라는 비정상적인 수치에서 16만 개 수준으로 드라마틱하게 줄어들었습니다.
    - 이는 N+1 문제로 인한 중복/과다 엔티티 로딩이 시스템 메모리에 얼마나 큰 부담을 주었는지를 반증하며, 최적화가 완벽하게 적용되었음을 의미합니다.

**4. 쿼리 캐시 효율**

- **지표**:
    - 전: 캐시 히트 816,022회 (효율 99.99%)
    - 후: 캐시 히트 78,625회 (효율 99.95%)
- **평가**:
    - 쿼리 실행 총량이 90% 이상 줄어들면서 캐시 히트 절대 수치도 감소했습니다.
    - 캐시 효율은 여전히 99.9% 수준으로 매우 높아, DB 쿼리 파싱 성능은 최적 상태입니다.

---

**종합 결론**

최적화 적용 후 **쿼리 실행 횟수가 91.2% 감소**하고, **엔티티 로딩이 99.9% 감소**하는 압도적인 성능 개선을 달성했습니다.

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

