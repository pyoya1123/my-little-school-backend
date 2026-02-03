# 🏫 마이 리틀 스쿨 (My Little School) - Backend

> **소규모 학교 학생들을 위한 메타버스 기반 소셜 학습 플랫폼**

학생들이 가상 공간에서 친구를 사귀고, 나만의 교실을 꾸미며, 퀴즈와 골든벨 등 다양한 활동에 참여할 수 있는 메타버스 서비스입니다.

---

## 📋 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [기술 스택](#-기술-스택)
3. [주요 기능](#-주요-기능)
4. [ERD](#-erd)
5. [API 문서](#-api-문서)
6. [성능 최적화](#-성능-최적화)
7. [부하 테스트 결과](#-부하-테스트-결과)
8. [프로젝트 실행 방법](#-프로젝트-실행-방법)

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

### 1. N+1 문제 해결

**문제 상황**
- 게시글 목록 조회 시 각 게시글의 댓글/좋아요를 개별 쿼리로 조회
- 게시글 100개 조회 시 → 301번의 쿼리 발생 (1 + 100 + 100 + 100)

**해결 방법**
```java
// Before: Lazy Loading으로 N+1 발생
@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
private List<Comment> comments;

// After: Fetch Join으로 한 번에 조회
@Query("SELECT b FROM Board b " +
       "LEFT JOIN FETCH b.comments " +
       "LEFT JOIN FETCH b.boardLikes " +
       "WHERE b.userId = :userId")
List<Board> findAllWithCommentsAndLikesByUserId(@Param("userId") Integer userId);
```

**결과**
| 지표 | 최적화 전 | 최적화 후 | 개선율 |
|------|-----------|-----------|--------|
| 쿼리 수 | 301회 | 1회 | **99.7% 감소** |
| 응답 시간 | 850ms | 45ms | **94.7% 감소** |

### 2. 인덱스 최적화

자주 조회되는 컬럼에 인덱스 추가:

```sql
-- 게시글 조회 최적화
CREATE INDEX idx_board_user_id ON board(user_id);
CREATE INDEX idx_board_created_at ON board(created_at DESC);

-- 댓글 조회 최적화
CREATE INDEX idx_comment_board_id ON comment(board_id);

-- 친구 관계 조회 최적화
CREATE INDEX idx_friendship_user_id ON friendship(user_id);
```

### 3. QueryDSL 도입

복잡한 동적 쿼리를 타입 안전하게 작성:

```java
public List<BoardListResponseDTO> getBoardListWithFilters(Integer userId, String keyword) {
    return queryFactory
        .select(Projections.constructor(BoardListResponseDTO.class,
            board.id,
            board.title,
            board.content,
            board.createdAt,
            comment.count(),
            boardLike.count()
        ))
        .from(board)
        .leftJoin(comment).on(comment.boardId.eq(board.id))
        .leftJoin(boardLike).on(boardLike.boardId.eq(board.id))
        .where(
            board.userId.eq(userId),
            keyword != null ? board.title.contains(keyword) : null
        )
        .groupBy(board.id)
        .orderBy(board.createdAt.desc())
        .fetch();
}
```

---

## 📈 부하 테스트 결과

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

