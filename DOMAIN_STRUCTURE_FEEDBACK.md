# 도메인 구조 및 연관관계 피드백

## 1. UserPosVisitCount 도메인 분리 여부

### 현재 구조
```
userposvisitcount/
  ├── controller/
  ├── domain/
  ├── dto/
  ├── repository/
  └── service/
```

### 분석 결과

#### ✅ 별도 도메인으로 분리하는 것이 적절한 경우
1. **독립적인 비즈니스 로직**이 있는 경우
2. **다른 여러 엔티티와 관계**를 맺는 경우
3. **복잡한 쿼리나 로직**이 필요한 경우

#### ❌ 현재 UserPosVisitCount의 문제점

**1. User에 강하게 종속적**
- User의 위치(mapType, userId)에 대한 방문 횟수만 저장
- User 없이는 의미가 없는 데이터
- User 삭제 시 함께 삭제되어야 함

**2. 단순한 카운터 기능**
- 단순히 count를 증가시키는 기능만 있음
- 복잡한 비즈니스 로직 없음

**3. 연관관계 부재**
- `Integer userId`만 저장 (JPA 연관관계 없음)
- 데이터 무결성 보장 불가

**4. 사용 빈도 낮음**
- UserService에서만 삭제 시 사용
- 별도 API는 있으나 사용 빈도가 낮을 것으로 예상

### 개선 방안

#### 방안 1: User 엔티티에 포함 (권장)
```java
// User.java에 추가
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<UserPosVisitCount> visitCounts = new ArrayList<>();
```

**장점**:
- User와의 관계가 명확해짐
- User 삭제 시 자동 삭제 (Cascade)
- 데이터 무결성 보장

**단점**:
- User 엔티티가 커질 수 있음 (하지만 현재는 문제 없음)

#### 방안 2: User 내부 클래스로 관리
```java
// User.java
@Embeddable
public static class VisitCount {
    private String mapType;
    private Integer count;
}

@ElementCollection
@CollectionTable(name = "user_pos_visit_count", 
                 joinColumns = @JoinColumn(name = "user_id"))
private Map<String, VisitCount> visitCounts = new HashMap<>();
```

**장점**:
- 더 간단한 구조
- User와 완전히 통합

**단점**:
- 복잡한 쿼리가 필요할 수 있음

#### 방안 3: 현재 구조 유지하되 연관관계 추가
```java
// UserPosVisitCount.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

// User.java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<UserPosVisitCount> visitCounts = new ArrayList<>();
```

**장점**:
- 현재 구조 유지하면서 연관관계만 추가
- 기존 코드 수정 최소화

**단점**:
- 별도 도메인으로 분리된 이유가 불명확

### 결론 및 권장사항

**권장**: **방안 1 또는 방안 3**

이유:
1. UserPosVisitCount는 User의 부가 정보에 가까움
2. 단순한 카운터 기능으로 별도 도메인 분리 필요성 낮음
3. 하지만 현재 구조를 유지한다면 최소한 연관관계는 추가해야 함

**면접 대비 관점**:
- "UserPosVisitCount를 별도 도메인으로 분리한 이유는?"
  → "처음에는 확장성을 고려했지만, 실제로는 User에 강하게 종속되어 있어 통합을 고려 중입니다."

---

## 2. School - User 연관관계 코드 분석

### 현재 구조

```java
// School.java
@OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
@JsonIgnore
private List<User> userList = new ArrayList<>();

// User.java
@ManyToOne
@JoinColumn(name = "school_id")
@JsonIgnore
School school;
```

### 발견된 문제점

#### ❌ 문제 1: 양방향 관계 수동 관리 (Critical)

**현재 코드**:
```java
// SchoolService.addUserToSchool() - 56-57줄
foundSchool.getUserList().add(foundUser);
foundUser.setSchool(foundSchool);
```

**문제점**:
- 양쪽을 모두 수동으로 업데이트해야 함
- 실수로 한쪽만 업데이트하면 데이터 불일치
- 코드 중복 가능성

**개선 방안**:
```java
// User.java에 헬퍼 메서드 추가
public void changeSchool(School school) {
    // 기존 학교에서 제거
    if (this.school != null) {
        this.school.getUserList().remove(this);
    }
    
    // 새 학교에 추가
    this.school = school;
    if (school != null) {
        school.getUserList().add(this);
    }
}

// SchoolService에서 사용
foundUser.changeSchool(foundSchool);
```

#### ❌ 문제 2: CascadeType.ALL 위험 (High)

**현재 코드**:
```java
@OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
private List<User> userList = new ArrayList<>();
```

**문제점**:
- School 삭제 시 모든 User가 삭제됨
- 의도치 않은 데이터 손실 위험
- 일반적으로 User는 School보다 생명주기가 길어야 함

**개선 방안**:
```java
@OneToMany(mappedBy = "school", cascade = {})  // 또는 CascadeType.PERSIST만
private List<User> userList = new ArrayList<>();
```

또는 School 삭제 전에 User의 school을 null로 설정하는 로직 추가

#### ❌ 문제 3: School.getUserList() 직접 조작 (Medium)

**현재 코드**:
```java
// SchoolService.deleteUserInUserList() - 93줄
boolean removed = school.getUserList().removeIf(user -> user.getId().equals(userId));
```

**문제점**:
- 컬렉션을 직접 조작하는 것은 위험
- 영속성 컨텍스트와 동기화 문제 가능성
- User의 school을 null로 설정하는 것이 더 안전

**개선 방안**:
```java
@Transactional
public void deleteUserInUserList(Integer schoolId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    
    if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
        throw new IllegalArgumentException("User is not in this school");
    }
    
    user.changeSchool(null);  // 헬퍼 메서드 사용
}
```

#### ❌ 문제 4: UserService와 SchoolService 중복 로직 (Medium)

**현재 코드**:
```java
// UserService.removeUser() - 237-238줄
if(user.getSchool() != null) {
    schoolService.deleteUserInUserList(user.getSchool().getId(), id);
}
```

**문제점**:
- User 삭제 시 School의 userList에서도 제거
- 하지만 User 삭제는 Cascade로 처리 가능
- 불필요한 서비스 호출

**개선 방안**:
- User 삭제 시 School의 userList에서 자동으로 제거되도록
- 또는 User 삭제 전에 school을 null로 설정

#### ❌ 문제 5: getUserListBySchoolId() 성능 문제 (Low)

**현재 코드**:
```java
public List<User> getUserListBySchoolId(Integer schoolId) {
    School foundSchool = schoolRepository.findById(schoolId).orElseThrow(...);
    return foundSchool.getUserList();  // LAZY 로딩 시 추가 쿼리 발생
}
```

**문제점**:
- School 조회 후 userList를 조회하면 추가 쿼리 발생
- 페치 조인 미사용

**개선 방안**:
```java
@Query("SELECT s FROM School s LEFT JOIN FETCH s.userList WHERE s.id = :schoolId")
School findByIdWithUsers(@Param("schoolId") Integer schoolId);
```

### 개선된 코드 예시

#### User.java
```java
@Entity
public class User {
    // ... 기존 필드들
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @JsonIgnore
    private School school;
    
    // 헬퍼 메서드 추가
    public void changeSchool(School newSchool) {
        // 기존 학교에서 제거
        if (this.school != null) {
            this.school.getUserList().remove(this);
        }
        
        // 새 학교에 추가
        this.school = newSchool;
        if (newSchool != null) {
            newSchool.getUserList().add(this);
        }
    }
    
    public void removeFromSchool() {
        changeSchool(null);
    }
}
```

#### School.java
```java
@Entity
public class School {
    // ... 기존 필드들
    
    @OneToMany(mappedBy = "school", cascade = {})  // CascadeType.ALL 제거
    @JsonIgnore
    private List<User> userList = new ArrayList<>();
    
    // 헬퍼 메서드 추가 (선택사항)
    public void addUser(User user) {
        if (!userList.contains(user)) {
            userList.add(user);
            user.setSchool(this);
        }
    }
}
```

#### SchoolService.java
```java
@Transactional
public SchoolDTO addUserToSchool(Integer schoolId, Integer userId, Integer gradeId) {
    School foundSchool = schoolRepository.findById(schoolId).orElseThrow(...);
    User foundUser = userRepository.findById(userId).orElseThrow(...);
    
    foundUser.changeSchool(foundSchool);  // 헬퍼 메서드 사용
    foundUser.setGrade(gradeId);
    
    return new SchoolDTO(foundSchool);
}

@Transactional
public void deleteUserInUserList(Integer schoolId, Integer userId) {
    User user = userRepository.findById(userId).orElseThrow(...);
    
    if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
        throw new IllegalArgumentException("User is not in this school");
    }
    
    user.removeFromSchool();  // 헬퍼 메서드 사용
}
```

### 결론 및 권장사항

#### School-User 연관관계 개선 체크리스트

**Critical (즉시 수정)**:
- [ ] `CascadeType.ALL` → `CascadeType.PERSIST` 또는 `{}`로 변경
- [ ] 양방향 관계 관리 헬퍼 메서드 추가

**High (빠른 시일 내)**:
- [ ] `getUserList()` 직접 조작 제거
- [ ] UserService와 SchoolService 중복 로직 정리

**Medium (점진적 개선)**:
- [ ] 페치 조인으로 성능 최적화
- [ ] School 삭제 시 User 처리 로직 추가

### 면접 대비 답변 예시

**Q: 양방향 관계를 어떻게 관리하셨나요?**
A: "처음에는 양쪽을 수동으로 업데이트했지만, 데이터 불일치 위험이 있어 User 엔티티에 `changeSchool()` 헬퍼 메서드를 추가하여 일관성을 보장하도록 개선했습니다."

**Q: CascadeType.ALL을 사용한 이유는?**
A: "처음에는 편의를 위해 ALL을 사용했지만, School 삭제 시 User가 함께 삭제되는 위험이 있어 PERSIST만 사용하도록 변경했습니다."

---

## 종합 권장사항

### UserPosVisitCount
1. **단기**: 연관관계 추가 (방안 3)
2. **장기**: User 엔티티에 통합 고려 (방안 1 또는 2)

### School-User 연관관계
1. **즉시**: CascadeType.ALL 제거
2. **즉시**: 헬퍼 메서드 추가
3. **단기**: 직접 조작 코드 제거
4. **단기**: 페치 조인 적용

