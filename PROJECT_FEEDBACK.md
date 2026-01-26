# 프로젝트 전체 피드백 리포트

## 📋 목차
1. [연관관계 매핑 문제](#연관관계-매핑-문제)
2. [아키텍처 및 설계 문제](#아키텍처-및-설계-문제)
3. [성능 최적화 문제](#성능-최적화-문제)
4. [코드 품질 문제](#코드-품질-문제)
5. [불필요한 코드](#불필요한-코드)
6. [중복 코드](#중복-코드)
7. [트랜잭션 관리 문제](#트랜잭션-관리-문제)
8. [기타 개선 사항](#기타-개선-사항)

---

## 연관관계 매핑 문제

### 1. UserPosVisitCount - User 연관관계 부재 (Critical)

**문제**:
- `UserPosVisitCount`가 `User`와 연관관계 없이 `Integer userId`만 저장
- JPA의 연관관계 매핑을 활용하지 않음

**현재 코드**:
```java
// UserPosVisitCount.java
@Column(name = "user_id")
private Integer userId;  // 연관관계 없음
```

**문제점**:
- 외래키 제약조건이 없어 데이터 무결성 보장 불가
- User 삭제 시 UserPosVisitCount가 자동 삭제되지 않음
- 조인 쿼리 작성이 복잡함

**개선 방안**:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

---

### 2. School - User 양방향 관계 관리 문제 (High)

**문제**:
- `School.getUserList()`를 직접 조작하는 코드 존재
- 양방향 관계의 일관성 유지가 어려움

**현재 코드**:
```java
// SchoolService.java:56
foundSchool.getUserList().add(foundUser);
foundUser.setSchool(foundSchool);

// SchoolService.java:93
school.getUserList().removeIf(user -> user.getId().equals(userId));
```

**문제점**:
- 양방향 관계를 수동으로 관리해야 함
- 실수로 한쪽만 업데이트하면 데이터 불일치 발생
- `CascadeType.ALL`로 인한 의도치 않은 삭제 위험

**개선 방안**:
- User 엔티티에 헬퍼 메서드 추가:
```java
// User.java
public void changeSchool(School school) {
    if (this.school != null) {
        this.school.getUserList().remove(this);
    }
    this.school = school;
    if (school != null) {
        school.getUserList().add(this);
    }
}
```

또는 단방향 관계로 변경 고려

---

### 3. School - Furniture 연관관계 문제 (Medium)

**문제**:
- `School`에 `@OneToMany(cascade = CascadeType.ALL)`로 Furniture 리스트가 있으나 `mappedBy` 없음
- 실제로 Furniture가 School을 참조하는지 불명확

**현재 코드**:
```java
// School.java:50
@OneToMany(cascade = CascadeType.ALL)
private List<Furniture> furnitureList = new ArrayList<>();
```

**문제점**:
- 양방향 관계인지 단방향 관계인지 불명확
- `mappedBy`가 없으면 조인 테이블이 생성될 수 있음

**개선 방안**:
- Furniture 엔티티 확인 후 적절한 매핑으로 수정
- 단방향이면 `@JoinColumn` 추가, 양방향이면 `mappedBy` 추가

---

### 4. EAGER 로딩 사용 (High)

**문제**:
- `User.interest`가 `@ElementCollection(fetch = FetchType.EAGER)`로 설정됨

**현재 코드**:
```java
// User.java:77
@ElementCollection(fetch = FetchType.EAGER)
@Column(name = "interest")
List<String> interest = new ArrayList<>();
```

**문제점**:
- User 조회 시 항상 interest를 함께 로드
- 불필요한 쿼리 발생
- N+1 문제 가능성

**개선 방안**:
```java
@ElementCollection(fetch = FetchType.LAZY)
```

필요한 경우에만 `@EntityGraph`로 로드

---

## 아키텍처 및 설계 문제

### 1. UserService 과도한 의존성 (Critical)

**문제**:
- `UserService`에 **20개 이상의 서비스 의존성**

**현재 의존성**:
```java
private final AIRecommendationService aiRecommendationService;
private final QuestService questService;
private final UserQuestService userQuestService;
private final InventoryService inventoryService;
private final AvatarService avatarService;
private final BoardService boardService;
private final ChatLogService chatLogService;
private final ChatBotLogService chatBotLogService;
private final FriendshipService friendshipService;
private final FurnitureService furnitureService;
private final GalleryService galleryService;
private final GuestBookService guestBookService;
private final MapContestService mapContestService;
private final NoteService noteService;
private final UserPosVisitCountService userPosVisitCountService;
private final BoardLikeService boardLikeService;
private final SchoolService schoolService;
private final EmotionAnalysisService emotionAnalysisService;
// ... 등등
```

**문제점**:
- 단일 책임 원칙(SRP) 위반
- 테스트 어려움
- 순환 참조 위험
- 높은 결합도

**개선 방안**:
1. **Facade 패턴 적용**: User 관련 모든 작업을 처리하는 UserFacade 생성
2. **이벤트 기반 아키텍처**: User 생성 시 이벤트 발행, 다른 서비스가 구독
3. **도메인 서비스 분리**: UserRegistrationService, UserDeletionService 등으로 분리

---

### 2. UserService.removeUser() 복잡도 (High)

**문제**:
- `removeUser()` 메서드가 15개 이상의 서비스를 직접 호출하여 삭제

**현재 코드**:
```java
@Transactional
public void removeUser(Integer id) {
    // 15개 이상의 서비스 호출
    aiRecommendationService.deleteRecommendationListByUserId(id);
    avatarService.deleteAvatar(id);
    boardService.deleteBoardListByUserId(id);
    // ... 등등
}
```

**문제점**:
- 하나의 메서드가 너무 많은 책임
- 트랜잭션 범위가 너무 큼
- 일부 실패 시 전체 롤백

**개선 방안**:
1. **Cascade 삭제 활용**: JPA Cascade로 자동 삭제되는 것은 제거
2. **이벤트 발행**: User 삭제 이벤트 발행, 각 서비스가 구독하여 처리
3. **비동기 처리**: 중요하지 않은 데이터는 비동기로 삭제

---

### 3. 응답 형식 불일치 (Medium)

**문제**:
- 컨트롤러마다 다른 응답 형식 사용

**예시**:
```java
// UserController.java
@GetMapping("/{userId}")
public UserDTO getUserById(...) {  // 직접 반환

@GetMapping("/list")
public ResponseResult<List<UserDTO>> getAllUsers() {  // ResponseResult 사용

@PostMapping
public ResponseEntity<UserDTO> registerUser(...) {  // ResponseEntity 사용
```

**개선 방안**:
- `ResponseResult<T>`로 통일
- 또는 `ResponseEntity<ResponseResult<T>>`로 통일

---

## 성능 최적화 문제

### 1. N+1 쿼리 문제 (Critical)

**문제 위치**:
- `BoardService.getAllBoards()` (89-96줄)
- `BoardService.getBoardListByUserId()` (32-40줄)

**현재 코드**:
```java
public List<BoardListResponseDTO> getAllBoards(Integer userId) {
    return boardRepository.findAll()
        .stream().map(b -> new BoardListResponseDTO(
                b,
                commentService.getCommentCountByBoardId(b.getId()),  // N+1
                boardLikeService.isExistLike(...)  // N+1
            )
        )
        .toList();
}
```

**문제점**:
- 게시판 100개 조회 시 → 1 + 100 + 100 = 201개 쿼리 실행

**개선 방안**:
```java
@Query("SELECT b, COUNT(c.id), " +
       "EXISTS(SELECT 1 FROM BoardLike bl WHERE bl.boardId = b.id AND bl.userId = :userId) " +
       "FROM Board b " +
       "LEFT JOIN Comment c ON c.boardId = b.id " +
       "GROUP BY b.id")
List<Object[]> findAllWithCounts(@Param("userId") Integer userId);
```

---

### 2. 페이징 부재 (High)

**문제**:
- `findAll()` 사용으로 대량 데이터 조회

**위치**:
- `UserService.getAllUser()` (77줄)
- `BoardService.getAllBoards()` (89줄)
- `ItemService.getAllItem()` (59줄)
- `QuestService.getAllQuest()` (69줄)
- `SchoolService.getAllSchool()` (76줄)

**개선 방안**:
```java
public Page<BoardDTO> getAllBoards(Integer userId, Pageable pageable) {
    return boardRepository.findAll(pageable)
        .map(b -> new BoardListResponseDTO(...));
}
```

---

### 3. 불필요한 쿼리 (Medium)

**문제**:
- `UserService.setAllUserStatusToOffline()` (320-327줄)

**현재 코드**:
```java
@Transactional
public void setAllUserStatusToOffline() {
    List<User> userList = userRepository.findAll();
    userList.forEach(user -> {
        user.setIsOnline(false);
        userRepository.save(user);  // 각각 save
    });
}
```

**문제점**:
- 모든 사용자를 조회한 후 각각 save
- 대량 업데이트 시 성능 저하

**개선 방안**:
```java
@Modifying
@Query("UPDATE User u SET u.isOnline = false")
void setAllUserStatusToOffline();
```

---

### 4. UserPosVisitCount 중복 조회 (Medium)

**문제**:
- `UserPosVisitCountService`에서 조회 후 없으면 생성하는 패턴이 중복

**현재 코드**:
```java
// getUserPosVisitCountByMapIdAndMapTypeAndUserId
UserPosVisitCount userPosVisitCount = repository.getUserPosVisitCountByMapIdAndMapTypeAndUserId(...);
if(userPosVisitCount == null) {
    userPosVisitCount = repository.save(new UserPosVisitCount(...));
}

// updateUserPosVisitCountByMapIdAndMapTypeAndUserId
UserPosVisitCount userPosVisitCount = repository.getUserPosVisitCountByMapIdAndMapTypeAndUserId(...);
if(userPosVisitCount == null) {
    userPosVisitCount = repository.save(new UserPosVisitCount(...));
}
```

**개선 방안**:
- 공통 메서드로 추출:
```java
private UserPosVisitCount getOrCreate(String mapType, int userId) {
    return repository.getUserPosVisitCountByMapIdAndMapTypeAndUserId(mapType, userId)
        .orElseGet(() -> repository.save(new UserPosVisitCount(mapType, userId, 0)));
}
```

---

## 코드 품질 문제

### 1. null 반환 (High)

**문제**:
- `UserService.getUser()`가 null 반환

**현재 코드**:
```java
public User getUser(Integer id) {
    return userRepository.findById(id).orElse(null);
}
```

**문제점**:
- NPE 위험
- 호출하는 쪽에서 null 체크 필요

**개선 방안**:
```java
public User getUser(Integer id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));
}
```

---

### 2. 예외 처리 불일치 (Medium)

**문제**:
- `IllegalStateException`, `IllegalArgumentException`, `RuntimeException` 혼용
- 커스텀 예외 `NotFoundException`이 정의되어 있으나 사용 안 함

**개선 방안**:
- 커스텀 예외 체계 구축:
  - `NotFoundException` - 404
  - `BadRequestException` - 400
  - `ConflictException` - 409

---

### 3. DTO 오타 (Low)

**문제**:
- `UserUpdateDTO.statusMesasge` (오타: Mesasge → Message)

**위치**: `UserUpdateDTO.java:25`

---

### 4. 주석 처리된 코드 (Low)

**문제**:
- 여러 곳에 주석 처리된 코드 존재

**예시**:
```java
// UserService.java:212
//    galleryService.deleteGalleryListByUserId(id);

// UserService.java:221-223
//    if(!mapContestService.getMapContestListByUserId(id).isEmpty()) {
//      mapContestService.deleteMapContestListByUserId(id);
//    }
```

**개선 방안**: 불필요한 주석 코드 제거

---

## 불필요한 코드

### 1. S3Config 전체 주석 처리 (Low)

**위치**: `S3Config.java`

**개선 방안**: 사용하지 않으면 삭제, 사용 예정이면 TODO 주석 추가

---

### 2. 중복된 Repository 메서드 (Medium)

**문제**:
- `UserRepository`에 유사한 메서드 2개

**현재 코드**:
```java
@Query("select u from User u where u.email = :userEmail")
User findByUserEmail(@Param("userEmail") String userEmail);

@Query("select u from User u where u.email = :email")
User getUserByEmail(@Param("email") String email);
```

**개선 방안**: 하나로 통일

---

### 3. 불필요한 import (Low)

**문제**:
- `UserPosVisitCountRepository.java:10`에 `@RestController` import (사용 안 함)

---

## 중복 코드

### 1. RestTemplate 중복 생성 (Medium)

**문제**:
- 여러 서비스에서 `RestTemplate`을 직접 생성

**위치**:
- `AIRecommendationService.java:46`
- `EmotionAnalysisService.java:29`

**개선 방안**:
- `AppConfig`에 이미 `RestTemplate` Bean이 있으므로 주입받아 사용

---

### 2. ObjectMapper 중복 생성 (Medium)

**문제**:
- 여러 서비스에서 `ObjectMapper`를 직접 생성

**위치**:
- `AIRecommendationService.java:47`
- `EmotionAnalysisService.java:30`

**개선 방안**:
- Bean으로 등록하여 주입받아 사용

---

### 3. HTTP 요청 코드 중복 (Medium)

**문제**:
- AI 서버 호출 코드가 여러 곳에 중복

**위치**:
- `AIRecommendationService.sendChatLogToAI()`
- `AIRecommendationService.sendInterestToAI()`
- `EmotionAnalysisService.RequestEmotionAnalysis()`

**개선 방안**:
- 공통 HTTP 클라이언트 서비스 생성

---

## 트랜잭션 관리 문제

### 1. 트랜잭션 범위 과다 (High)

**문제**:
- `UserService.removeUser()`가 하나의 트랜잭션에서 15개 이상의 작업 수행

**문제점**:
- 트랜잭션 락 시간 증가
- 일부 실패 시 전체 롤백

**개선 방안**:
- 중요 작업만 트랜잭션으로 묶고, 나머지는 비동기 처리

---

### 2. 트랜잭션 누락 (Medium)

**문제**:
- 일부 메서드에 `@Transactional` 누락

**예시**:
- `BoardService.registerBoard()` (26줄)
- `BoardService.getBoardListByUserId()` (32줄)

---

### 3. readOnly 트랜잭션 미사용 (Low)

**문제**:
- 조회 메서드에 `@Transactional(readOnly = true)` 미사용

**개선 방안**:
```java
@Transactional(readOnly = true)
public List<BoardDTO> getAllBoards(...) {
    // ...
}
```

---

## 기타 개선 사항

### 1. System.out.println 사용 (High)

**문제**:
- 약 45곳에서 `System.out.println`, `System.err.println`, `printStackTrace()` 사용

**개선 방안**: SLF4J Logger 사용

---

### 2. 하드코딩된 값 (Medium)

**문제**:
- 매직 넘버/문자열 사용

**예시**:
```java
user.setGold(100000);  // UserService.java:269
user.setMaxExp(100);   // UserService.java:273
```

**개선 방안**: 상수 클래스로 분리

---

### 3. 네이밍 불일치 (Low)

**문제**:
- 메서드명이 일관되지 않음

**예시**:
- `getUserByEmail()` vs `findByUserEmail()`
- `RequestEmotionAnalysis()` (대문자 시작)

**개선 방안**: 네이밍 컨벤션 통일

---

### 4. 불필요한 의존성 (Low)

**문제**:
- `build.gradle`에 사용하지 않는 의존성

**예시**:
- `spring-boot-starter-thymeleaf` (사용 안 함)
- `spring-boot-starter-web-services` (사용 안 함)

---

## 우선순위별 개선 권장사항

### Critical (즉시 수정)
1. ✅ UserPosVisitCount - User 연관관계 추가
2. ✅ UserService 의존성 분리 (Facade 패턴 또는 이벤트 기반)
3. ✅ N+1 쿼리 문제 해결 (BoardService)
4. ✅ null 반환 제거

### High (빠른 시일 내)
5. ✅ School-User 양방향 관계 개선
6. ✅ 페이징 적용
7. ✅ EAGER → LAZY 변경
8. ✅ UserService.removeUser() 리팩토링
9. ✅ System.out.println → Logger

### Medium (점진적 개선)
10. ✅ 응답 형식 통일
11. ✅ 예외 처리 개선
12. ✅ 트랜잭션 범위 최적화
13. ✅ 중복 코드 제거
14. ✅ 불필요한 쿼리 최적화

### Low (여유 있을 때)
15. ✅ 주석 코드 제거
16. ✅ 하드코딩된 값 상수화
17. ✅ 네이밍 통일
18. ✅ 불필요한 의존성 제거

---

## 요약

### 가장 심각한 문제 Top 5
1. **UserService 과도한 의존성** - 아키텍처 재설계 필요
2. **N+1 쿼리 문제** - 성능에 직접적 영향
3. **UserPosVisitCount 연관관계 부재** - 데이터 무결성 문제
4. **페이징 부재** - 대량 데이터 처리 시 문제
5. **null 반환** - NPE 위험

### 개선 시 예상 효과
- **성능**: N+1 문제 해결 시 50-90% 성능 향상 예상
- **유지보수성**: UserService 리팩토링 시 테스트 용이성 증가
- **안정성**: 연관관계 개선 시 데이터 무결성 보장

