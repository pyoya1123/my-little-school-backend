# 프론트엔드 개발 프롬프트

## 프로젝트 개요

**프로젝트명**: MyLittleSchool (학교 매칭 플랫폼)

**프로젝트 설명**: 
메타버스 2D 환경의 학교 매칭 플랫폼입니다. 학생들이 학교를 선택하고, 친구를 사귀며, 퀘스트를 수행하고, 가구를 배치하여 자신만의 교실을 꾸밀 수 있는 소셜 플랫폼입니다.

**원래 클라이언트**: Unity (2D 메타버스)
**목표**: 웹 프론트엔드로 마이그레이션

---

## 백엔드 정보

### 기술 스택
- **프레임워크**: Spring Boot 3.3.4
- **데이터베이스**: MySQL
- **실시간 통신**: WebSocket (Spring WebSocket)
- **API 스타일**: RESTful API
- **문서화**: Swagger/OpenAPI (http://localhost:8080/swagger-ui-custom.html)
- **인증**: 현재 비활성화 (추후 JWT 구현 예정)

### Base URL
```
http://localhost:8080
```

### API 응답 형식

#### 표준 응답 형식 (일부 API)
```typescript
interface ResponseResult<T> {
  success: boolean;
  response: T;
  error: {
    message: string;
    status: number;
  } | null;
}
```

#### 직접 DTO 반환 (일부 API)
일부 API는 `ResponseResult`로 감싸지 않고 직접 DTO를 반환합니다.

---

## 주요 API 엔드포인트

### 1. 사용자 관리 (User)

#### Base Path: `/user`

**회원가입**
- `POST /user`
- Request Body:
```typescript
interface UserRegisterDTO {
  name: string;
  grade: number;
  birthday: string;
  gender: boolean;
  email: string;
  password: string;
  phone: string;
  statusMessage: string;
  gold?: number;
  interest: string[];
  schoolId?: number;
}
```
- Response: `UserDTO` (직접 반환)

**사용자 조회**
- `GET /user/{userId}` → `UserDTO`
- `GET /user/email/{userEmail}` → `UserDTO`
- `GET /user/list` → `ResponseResult<UserDTO[]>`
- `GET /user/is-exist/{userEmail}` → `boolean`

**사용자 정보 수정**
- `PATCH /user`
- Request Body:
```typescript
interface UserUpdateDTO {
  id: number;
  name?: string;
  grade?: number;
  birthday?: string;
  gender?: boolean;
  email?: string;
  password?: string;
  phone?: string;
  interest?: string[];
  statusMesasge?: string;
  gold?: number;
  schoolId?: number;
}
```

**학교 등록**
- `PATCH /user/register-school`
- Request Body:
```typescript
interface UserRegisterSchoolDTO {
  userId: number;
  schoolId: number;
}
```

**프로필**
- `GET /user/profile/{userId}` → `ResponseResult<UserProfileDTO>`
- `PATCH /user/profile` → `ResponseResult<UserProfileDTO>`

**위치 정보**
- `GET /user/pos/{userId}` → `UserPosDTO`
- `PATCH /user/pos` → `ResponseResult<UserPosDTO>`
- Request Body:
```typescript
interface UserPosUpdateDTO {
  userId: number;
  mapId?: number;
  mapType?: string;
}
```

**사용자 삭제**
- `DELETE /user?userId={id}` → `204 No Content`

#### DTO 구조
```typescript
interface UserDTO {
  id: number;
  name: string;
  grade: number;
  birthday: string;
  gender: boolean;
  email: string;
  password: string; // 보안상 프론트엔드에서 사용하지 않음
  phone: string;
  statusMesasge: string;
  gold: number;
  interest: string[];
  isOnline: boolean;
  mapId: number;
  mapType: string;
  school: SchoolResponseDTO | null;
}

interface UserProfileDTO {
  id: number;
  name: string;
  interest: string[];
  statusMessage: string;
}

interface UserPosDTO {
  userId: number;
  mapId: number;
  mapType: string;
}
```

---

### 2. 학교 관리 (School)

#### Base Path: `/school`

**학교 목록 조회**
- `GET /school/list` → `ResponseResult<SchoolDTO[]>`
- `GET /school?schoolId={id}` → `SchoolDTO`
- `GET /school/{schoolName}` → `SchoolResponseDTO[]` (이름으로 검색)
- `GET /school/nearby/{schoolName}/{radius}` → `SchoolDTO[]` (반경 검색)

**학교 등록**
- `POST /school`
- Request Body:
```typescript
interface SchoolRegisterDTO {
  schoolName: string;
  location: string;
  longitude: number;
  latitude: number;
}
```

**학교에 사용자 추가**
- `POST /school/add-user?schoolId={id}&userId={id}&user_grade={grade}`

**학교의 사용자 목록**
- `GET /school/user-list?schoolId={id}` → `UserDTO[]`

**학교 삭제**
- `DELETE /school?schoolId={id}`

#### DTO 구조
```typescript
interface SchoolDTO {
  id: number;
  schoolName: string;
  location: string;
  longitude: number;
  latitude: number;
}

interface SchoolResponseDTO {
  id: number;
  schoolName: string;
  location: string;
  userCount: number;
}
```

---

### 3. 친구 시스템 (Friendship)

#### Base Path: `/friendship`

**친구 목록 조회**
- `GET /friendship/list?userId={id}` → `ResponseResult<FriendshipResponseDTO[]>`

**친구 요청 목록**
- `GET /friendship/list-receiver-unaccepted/{receiverId}` → `ResponseResult<FriendshipResponseDTO[]>` (받은 요청)
- `GET /friendship/list-requester-unaccepted/{requesterId}` → `ResponseResult<FriendshipResponseDTO[]>` (보낸 요청)

**친구 요청 보내기**
- `POST /friendship/request`
- Request Body:
```typescript
interface FriendshipRequestDTO {
  requesterId: number;
  receiverId: number;
  message?: string;
}
```

**친구 요청 수락/거절/취소**
- `POST /friendship/accept?friendshipId={id}` → `"친구 요청을 수락했습니다."`
- `POST /friendship/reject?friendshipId={id}` → `"친구 요청을 거절했습니다."`
- `POST /friendship/cancel?friendshipId={id}` → `"친구 요청을 취소했습니다."`

**친구 삭제**
- `DELETE /friendship/remove?friendshipId={id}` → `"친구 삭제 완료"`

**친구 요청 메시지 수정**
- `PATCH /friendship/message`
- Request Body:
```typescript
interface FriendshipUpdateDTO {
  friendshipId: number;
  message: string;
}
```

#### DTO 구조
```typescript
interface FriendshipResponseDTO {
  friendshipId: number;
  requesterId: number;
  receiverId: number;
  requesterName: string;
  receiverName: string;
  message: string;
  status: string; // "PENDING", "ACCEPTED", "REJECTED"
}
```

---

### 4. 게시판 (Board)

#### Base Path: `/board`

**게시글 등록**
- `POST /board`
- Request Body:
```typescript
interface BoardRegisterDTO {
  title: string;
  content: string;
  userId: number;
}
```

**게시글 조회**
- `GET /board/{boardId}` → `BoardDTO`
- `GET /board/all-list/{userId}` → `BoardListResponseDTO[]` (전체 게시글)
- `GET /board/list/{userId}` → `BoardListResponseDTO[]` (내 게시글)

**게시글 수정**
- `PATCH /board`
- Request Body:
```typescript
interface BoardUpdateDTO {
  id: number;
  title?: string;
  content?: string;
}
```

**게시글 삭제**
- `DELETE /board/{boardId}` → `204 No Content`

**게시글 좋아요**
- `POST /board-like` (Base Path: `/board-like`)
- Request Body:
```typescript
interface BoardAddLikeDTO {
  boardId: number;
  userId: number;
}
```

- `DELETE /board-like` (Base Path: `/board-like`)
- Request Body:
```typescript
interface BoardRemoveLikeDTO {
  boardId: number;
  userId: number;
}
```

#### DTO 구조
```typescript
interface BoardDTO {
  boardId: number;
  title: string;
  content: string;
  likeCount: number;
  commentCount: number;
}

interface BoardListResponseDTO {
  boardId: number;
  title: string;
  content: string;
  likeCount: number;
  commentCount: number;
  userId: number;
  userName: string;
}
```

---

### 5. 댓글 (Comment)

#### Base Path: `/comment`

**댓글 등록**
- `POST /comment`
- Request Body:
```typescript
interface CommentRequestDTO {
  boardId: number;
  userId: number;
  content: string;
}
```

**댓글 조회**
- `GET /comment?boardId={id}` → `CommentResponseDTO[]`

**댓글 수정**
- `PATCH /comment`
- Request Body:
```typescript
interface CommentUpdateDTO {
  id: number;
  content: string;
}
```

**댓글 삭제**
- `DELETE /comment?commentId={id}`

#### DTO 구조
```typescript
interface CommentResponseDTO {
  id: number;
  boardId: number;
  userId: number;
  userName: string;
  content: string;
}
```

---

### 6. 가구 배치 (Furniture)

#### Base Path: `/furniture`

**가구 배치**
- `POST /furniture`
- Request Body:
```typescript
interface FurnitureRegisterDTO {
  objId: number; // 아이템 ID
  x: number;
  y: number;
  rot: number; // 회전 각도
  flip: boolean;
  mapId: number;
  mapType: string; // "MyClassroom", "School" 등
  userId: number;
}
```

**가구 조회**
- `GET /furniture/list?userId={id}&mapId={id}&mapType={type}` → `FurnitureDTO[]`

**가구 수정**
- `PATCH /furniture`
- Request Body: `FurnitureDTO`

**가구 삭제**
- `DELETE /furniture?furnitureId={id}`

#### DTO 구조
```typescript
interface FurnitureDTO {
  id: number;
  objId: number;
  x: number;
  y: number;
  rot: number;
  flip: boolean;
  mapId: number;
  mapType: string;
}
```

---

### 7. 아이템/인벤토리 (Item/Inventory)

#### Base Path: `/item`, `/inventory`

**아이템 조회**
- `GET /item/list` → `ItemDTO[]`
- `GET /item/list/{itemType}` → `ItemDTO[]`
- `GET /item/{itemId}` → `ItemDTO`

**인벤토리 조회**
- `GET /inventory?userId={id}` → `InventoryDTO[]`

**인벤토리 아이템 추가/수정**
- `PATCH /inventory`
- Request Body:
```typescript
interface InventoryUpdateDTO {
  userId: number;
  itemId: number;
  quantity: number;
}
```

#### DTO 구조
```typescript
interface ItemDTO {
  id: number;
  itemIdx: number;
  itemName: string;
  price: number;
  itemType: string;
}

interface InventoryDTO {
  id: number;
  userId: number;
  itemId: number;
  itemName: string;
  quantity: number;
}
```

---

### 8. 퀘스트 (Quest)

#### Base Path: `/quest`, `/user-quest`

**퀘스트 조회**
- `GET /quest/list` → `QuestDTO[]`
- `GET /quest/list/{questType}` → `QuestDTO[]` (예: "TUTORIAL")

**사용자 퀘스트**
- `GET /user-quest/list/{userId}` → `UserQuestDTO[]`
- `GET /user-quest/list/completed/{userId}` → `UserQuestDTO[]`
- `GET /user-quest/is-complete/{questId}` → `boolean`

**퀘스트 등록**
- `POST /user-quest`
- Request Body:
```typescript
interface UserQuestRegisterRequestDTO {
  questId: number;
  userId: number;
}
```

#### DTO 구조
```typescript
interface QuestDTO {
  questId: number;
  questTitle: string;
  questDescription: string;
  questType: string;
  expReward: number;
  goldReward: number;
}

interface UserQuestDTO {
  userQuestId: number;
  questId: number;
  userId: number;
  isComplete: boolean;
  questTitle: string;
  questDescription: string;
}
```

---

### 9. 채팅 로그 (ChatLog)

#### Base Path: `/chat-log`

**채팅 로그 저장**
- `POST /chat-log`
- Request Body:
```typescript
interface ChatLogRequestDTO {
  senderId: number;
  receiverId: number;
  message: string;
  chatType: string; // "DIRECT", "GROUP" 등
}
```

**채팅 로그 조회**
- `GET /chat-log?senderId={id}` → `ChatLogResponseDTO[]`
- `GET /chat-log/list` → `ResponseResult<ChatLogResponseDTO[]>`

#### DTO 구조
```typescript
interface ChatLogResponseDTO {
  id: number;
  senderId: number;
  receiverId: number;
  message: string;
  timestamp: string;
  chatType: string;
}
```

---

### 10. 기타 기능

#### 방명록 (GuestBook)
- Base Path: `/guest-book`
- `POST /guest-book`, `GET /guest-book/list/{userId}`, `DELETE /guest-book?guestBookId={id}`

#### 쪽지 (Note)
- Base Path: `/note`
- `POST /note`, `GET /note/list/{userId}`, `DELETE /note?noteId={id}`

#### 아바타 (Avatar)
- Base Path: `/avatar`
- `GET /avatar/{userId}`, `POST /avatar`, `PATCH /avatar`

#### 갤러리 (Gallery)
- Base Path: `/gallery`
- `POST /gallery`, `GET /gallery/list/{schoolId}`, `GET /gallery/{galleryId}`

#### 맵 콘테스트 (MapContest)
- Base Path: `/map-contest`
- `GET /map-contest/list`, `POST /map-contest`, `GET /map-contest/furniture-list/{mapContestId}`

---

## WebSocket 실시간 통신

### 연결 정보
- **URL**: `ws://localhost:8080/ws?userId={userId}&mapId={mapId}&mapType={mapType}`
- **프로토콜**: 표준 WebSocket (텍스트 메시지)

### 메시지 형식
모든 메시지는 JSON 형식으로 주고받습니다.

### 주요 메시지 타입

#### 1. 친구 요청 관련
```typescript
// 친구 요청 보내기
{
  "type": "FRIEND_REQUEST",
  "requesterId": number,
  "receiverId": number,
  "message": string
}

// 친구 요청 수락
{
  "type": "FRIEND_ACCEPT",
  "friendshipId": number
}

// 친구 요청 거절
{
  "type": "FRIEND_REJECT",
  "friendshipId": number
}

// 친구 요청 삭제
{
  "type": "DELETE_FRIEND_REQUEST",
  "friendshipId": number
}

// 친구 관계 삭제
{
  "type": "DELETE_FRIENDSHIP",
  "friendshipId": number
}
```

#### 2. 친구 목록 조회
```typescript
// 보낸 요청
{
  "type": "FETCH_PENDING_REQUESTS_BY_REQUESTER",
  "userId": number
}

// 받은 요청
{
  "type": "FETCH_PENDING_REQUESTS",
  "userId": number
}

// 친구 목록
{
  "type": "FETCH_FRIEND_LIST",
  "userId": number
}
```

#### 3. 위치 정보
```typescript
// 위치 정보 업데이트
{
  "type": "USER_POS_INFO",
  "userId": number,
  "mapId": number,
  "mapType": string
}

// 위치 정보 조회
{
  "type": "GET_USER_POS",
  "userId": number
}

// 응답
{
  "type": "ACCEPT_USER_POS",
  "userId": number,
  "mapId": number,
  "mapType": string
}
```

#### 4. 온라인 상태
```typescript
// 연결 시 자동으로 전송됨
{
  "type": "CHECK_USER_ONLINE_STATUS",
  "userId": number,
  "mapId": number,
  "mapType": string,
  "status": "online"
}

// 친구들의 상태 브로드캐스트 (자동 수신)
{
  "type": "USER_STATUS_UPDATE",
  // 친구들의 상태 정보 배열
}
```

#### 5. Keep-Alive
서버에서 50초마다 자동으로 전송:
```typescript
{
  "type": "KEEP_ALIVE"
}
```

---

## 프론트엔드 기술 스택 요구사항

### 필수
- **프레임워크**: React 18+ (권장) 또는 Vue 3
- **상태 관리**: Zustand (React) 또는 Pinia (Vue)
- **라우팅**: React Router 또는 Vue Router
- **HTTP 클라이언트**: axios
- **WebSocket**: 브라우저 네이티브 WebSocket API 또는 socket.io-client

### 권장
- **UI 라이브러리**: Material-UI, Ant Design, 또는 Tailwind CSS
- **폼 관리**: react-hook-form (React) 또는 VeeValidate (Vue)
- **지도**: react-kakao-maps-sdk (Kakao Map API)
- **드래그 앤 드롭**: react-beautiful-dnd 또는 @dnd-kit/core
- **이미지 업로드**: react-dropzone

### 선택사항
- **타입스크립트**: 강력 권장
- **상태 관리**: Redux Toolkit (복잡한 경우)
- **테스트**: Vitest, React Testing Library

---

## 구현해야 할 주요 페이지/컴포넌트

### 1. 인증 관련
- [ ] 로그인 페이지 (`/login`)
- [ ] 회원가입 페이지 (`/register`)
- [ ] 이메일 중복 확인 기능

### 2. 사용자 관련
- [ ] 프로필 페이지 (`/profile/:userId`)
- [ ] 프로필 수정 페이지
- [ ] 사용자 목록 페이지

### 3. 학교 관련
- [ ] 학교 검색 페이지 (`/school/search`)
- [ ] 학교 목록 페이지 (`/school`)
- [ ] 학교 상세 페이지 (`/school/:id`)
- [ ] 지도에서 학교 위치 표시 (Kakao Map)

### 4. 친구 시스템
- [ ] 친구 목록 페이지 (`/friends`)
- [ ] 친구 요청 목록 페이지 (`/friends/requests`)
- [ ] 친구 검색 기능
- [ ] WebSocket 실시간 알림

### 5. 게시판
- [ ] 게시글 목록 페이지 (`/board`)
- [ ] 게시글 작성 페이지 (`/board/write`)
- [ ] 게시글 상세 페이지 (`/board/:id`)
- [ ] 댓글 기능
- [ ] 좋아요 기능

### 6. 메타버스 기능
- [ ] 내 교실 페이지 (`/my-classroom`)
  - 가구 배치 (드래그 앤 드롭)
  - 인벤토리 표시
  - 가구 배치 저장
- [ ] 학교 공간 페이지 (`/school/:id/space`)
- [ ] 만남의 광장 페이지 (`/meeting-hall`)

### 7. 퀘스트
- [ ] 퀘스트 목록 페이지 (`/quest`)
- [ ] 퀘스트 상세 모달/페이지
- [ ] 퀘스트 진행 상황 표시

### 8. 기타
- [ ] 채팅 페이지 (`/chat`)
- [ ] 갤러리 페이지 (`/gallery`)
- [ ] 쪽지 페이지 (`/notes`)
- [ ] 방명록 페이지 (`/guestbook/:userId`)

---

## 주요 기능 요구사항

### 1. 실시간 기능 (WebSocket)
- 친구 요청 시 실시간 알림
- 친구 온라인/오프라인 상태 실시간 업데이트
- 위치 정보 실시간 동기화
- 연결 끊김 시 자동 재연결

### 2. 맵 시스템
- Unity의 2D 맵을 웹 페이지로 전환
- `mapType`을 라우트로 매핑:
  - `"MyClassroom"` → `/my-classroom`
  - `"School"` → `/school/:id`
  - `"MeetingHall"` → `/meeting-hall`
- 각 맵에서 해당 맵의 사용자 목록 표시

### 3. 가구 배치 시스템
- 드래그 앤 드롭으로 가구 배치
- 좌표 저장 (x, y)
- 회전 기능 (rot)
- 뒤집기 기능 (flip)
- 미리보기 기능

### 4. 아바타 시스템
- 아바타 에디터 페이지
- 부위별 선택 UI
- 실시간 미리보기

### 5. 학교 매칭
- 지도에서 학교 위치 표시
- 반경 검색 기능
- 학교 정보 표시

---

## 디자인 가이드라인

### 컬러 팔레트 (제안)
- **주 색상**: 학교 느낌의 파란색 계열
- **보조 색상**: 밝은 노란색, 초록색
- **배경**: 밝고 깔끔한 화이트/라이트 그레이

### UI/UX 원칙
1. **친근하고 접근하기 쉬운 디자인**
2. **모바일 우선 반응형 디자인**
3. **직관적인 네비게이션**
4. **명확한 피드백** (로딩, 성공, 에러)

### 컴포넌트 스타일
- 카드 기반 레이아웃
- 둥근 모서리
- 부드러운 그림자
- 호버 효과

---

## 에러 처리

### HTTP 에러 코드
- `400 Bad Request`: 잘못된 요청
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 충돌 (예: 이미 친구 요청 존재)
- `500 Internal Server Error`: 서버 오류

### 에러 응답 형식
```typescript
// ResponseResult 형식
{
  "success": false,
  "response": null,
  "error": {
    "message": "에러 메시지",
    "status": 400
  }
}

// 또는 직접 문자열 반환 (일부 API)
"에러 메시지"
```

---

## 개발 시 주의사항

### 1. CORS
- 현재 백엔드는 `setAllowedOrigins("*")`로 설정되어 있음
- 개발 환경에서는 문제없지만, 프로덕션에서는 특정 도메인만 허용하도록 변경 필요

### 2. 인증
- 현재 인증이 비활성화되어 있음
- 추후 JWT 기반 인증이 추가될 예정
- 일단은 userId를 직접 전달하는 방식으로 개발

### 3. 파일 업로드
- Cloudinary를 사용하여 이미지 업로드
- 최대 파일 크기: 15MB
- Base64 인코딩 또는 FormData 사용

### 4. 페이징
- 현재 대부분의 목록 API에 페이징이 없음
- 대량 데이터 조회 시 성능 이슈 가능
- 프론트엔드에서 가상 스크롤 또는 무한 스크롤 고려

### 5. WebSocket 재연결
- 브라우저 탭 전환, 네트워크 끊김 시 자동 재연결 처리 필요
- Keep-Alive 메시지로 연결 상태 확인

---

## 구현 우선순위

### Phase 1: 핵심 기능 (MVP)
1. 사용자 인증/프로필
2. 학교 검색 및 등록
3. 친구 시스템 (기본)
4. 게시판 (기본)

### Phase 2: 소셜 기능
1. 친구 시스템 (실시간 알림)
2. 게시판 (댓글, 좋아요)
3. 쪽지/방명록

### Phase 3: 메타버스 기능
1. 맵 시스템
2. 가구 배치
3. 아바타 커스터마이징

### Phase 4: 고급 기능
1. 퀘스트 시스템
2. 갤러리
3. 맵 콘테스트

---

## 추가 정보

### Swagger 문서
백엔드 서버 실행 후 다음 URL에서 API 문서 확인 가능:
```
http://localhost:8080/swagger-ui-custom.html
```

### 프로파일링 엔드포인트
- `GET /profiling/hibernate-stats` - Hibernate 통계
- `GET /actuator/health` - 헬스 체크
- `GET /actuator/metrics` - 메트릭 정보

---

## 코드 예시

### 1. API 클라이언트 설정 (axios)

```typescript
// api/client.ts
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ResponseResult 타입 처리 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    // ResponseResult 형식인 경우
    if (response.data && typeof response.data === 'object' && 'success' in response.data) {
      if (response.data.success) {
        return response.data.response;
      } else {
        throw new Error(response.data.error?.message || '요청 실패');
      }
    }
    // 직접 DTO 반환인 경우
    return response.data;
  },
  (error) => {
    return Promise.reject(error);
  }
);
```

### 2. 사용자 API 예시

```typescript
// api/user.ts
import { apiClient } from './client';

export const userApi = {
  // 회원가입
  register: async (data: UserRegisterDTO) => {
    const response = await apiClient.post<UserDTO>('/user', data);
    return response;
  },

  // 사용자 조회
  getUser: async (userId: number) => {
    const response = await apiClient.get<UserDTO>(`/user/${userId}`);
    return response;
  },

  // 프로필 조회
  getProfile: async (userId: number) => {
    const response = await apiClient.get<ResponseResult<UserProfileDTO>>(`/user/profile/${userId}`);
    return response;
  },

  // 위치 업데이트
  updatePosition: async (data: UserPosUpdateDTO) => {
    const response = await apiClient.patch<ResponseResult<UserPosDTO>>('/user/pos', data);
    return response;
  },
};
```

### 3. WebSocket 연결 예시

```typescript
// websocket/client.ts
class WebSocketClient {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  connect(userId: number, mapId: number, mapType: string) {
    const wsUrl = `ws://localhost:8080/ws?userId=${userId}&mapId=${mapId}&mapType=${mapType}`;
    
    this.ws = new WebSocket(wsUrl);

    this.ws.onopen = () => {
      console.log('WebSocket 연결 성공');
      this.reconnectAttempts = 0;
    };

    this.ws.onmessage = (event) => {
      const message = JSON.parse(event.data);
      this.handleMessage(message);
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket 에러:', error);
    };

    this.ws.onclose = () => {
      console.log('WebSocket 연결 종료');
      this.reconnect(userId, mapId, mapType);
    };
  }

  private reconnect(userId: number, mapId: number, mapType: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => {
        console.log(`재연결 시도 ${this.reconnectAttempts}...`);
        this.connect(userId, mapId, mapType);
      }, 3000);
    }
  }

  send(message: object) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    }
  }

  // 친구 요청 보내기
  sendFriendRequest(requesterId: number, receiverId: number, message?: string) {
    this.send({
      type: 'FRIEND_REQUEST',
      requesterId,
      receiverId,
      message: message || '',
    });
  }

  // 친구 요청 수락
  acceptFriendRequest(friendshipId: number) {
    this.send({
      type: 'FRIEND_ACCEPT',
      friendshipId,
    });
  }

  // 위치 정보 업데이트
  updatePosition(userId: number, mapId: number, mapType: string) {
    this.send({
      type: 'USER_POS_INFO',
      userId,
      mapId,
      mapType,
    });
  }

  private handleMessage(message: any) {
    switch (message.type) {
      case 'KEEP_ALIVE':
        // 연결 유지 확인
        break;
      case 'ACCEPT_FRIEND_POS_INFO':
        // 친구 위치 정보 업데이트
        this.onFriendPositionUpdate?.(message);
        break;
      case 'USER_STATUS_UPDATE':
        // 친구 상태 업데이트
        this.onFriendStatusUpdate?.(message);
        break;
      // ... 기타 메시지 타입 처리
    }
  }

  onFriendPositionUpdate?: (message: any) => void;
  onFriendStatusUpdate?: (message: any) => void;

  disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }
}

export const wsClient = new WebSocketClient();
```

### 4. React 컴포넌트 예시

```typescript
// components/FriendList.tsx
import { useEffect, useState } from 'react';
import { wsClient } from '../websocket/client';
import { friendshipApi } from '../api/friendship';

export const FriendList = ({ userId }: { userId: number }) => {
  const [friends, setFriends] = useState<FriendshipResponseDTO[]>([]);
  const [onlineFriends, setOnlineFriends] = useState<Set<number>>(new Set());

  useEffect(() => {
    // 친구 목록 조회
    friendshipApi.getFriends(userId).then(setFriends);

    // WebSocket 메시지 핸들러 등록
    wsClient.onFriendStatusUpdate = (message) => {
      // 친구 상태 업데이트 처리
      if (message.status === 'online') {
        setOnlineFriends(prev => new Set(prev).add(message.userId));
      } else {
        setOnlineFriends(prev => {
          const next = new Set(prev);
          next.delete(message.userId);
          return next;
        });
      }
    };
  }, [userId]);

  return (
    <div>
      <h2>친구 목록</h2>
      {friends.map(friend => (
        <div key={friend.friendshipId}>
          <span>{friend.receiverName}</span>
          {onlineFriends.has(friend.receiverId) && (
            <span className="online-badge">온라인</span>
          )}
        </div>
      ))}
    </div>
  );
};
```

---

## 예상 개발 시간

- **MVP (Phase 1)**: 2-3주
- **Phase 2**: 2-3주
- **Phase 3**: 3-4주
- **Phase 4**: 2-3주

**총 예상 시간**: 9-13주 (약 2-3개월)

---

## 추가 참고사항

### 백엔드 서버 실행
1. MySQL 데이터베이스 설정 필요
2. 환경 변수 설정 (.env 파일)
3. `./gradlew bootRun` 또는 IDE에서 실행
4. 기본 포트: 8080

### Swagger 문서
서버 실행 후 다음 URL에서 모든 API 확인 가능:
```
http://localhost:8080/swagger-ui-custom.html
```

### 테스트 데이터 생성
백엔드에 테스트 데이터 생성 API가 있습니다:
- `POST /test-data/users?count={number}` - 사용자 생성
- `POST /test-data/boards?count={number}&userId={id}` - 게시글 생성

---

## 질문이나 도움이 필요하면

백엔드 개발자에게 문의하거나 Swagger 문서를 참고하세요.

**중요**: 이 프롬프트는 백엔드 API를 기반으로 작성되었습니다. 실제 구현 시 Swagger 문서를 참고하여 정확한 API 스펙을 확인하세요.

**프로젝트 목표**: Unity 클라이언트에서 웹 프론트엔드로 마이그레이션하여 혼자 개발을 이어나가기 위한 프로젝트입니다. 핵심 기능에 집중하여 MVP를 먼저 완성하는 것을 권장합니다.

