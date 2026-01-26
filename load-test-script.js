import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const requestDuration = new Trend('request_duration');
const requestCount = new Counter('request_count');

// 테스트 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  // 부하 테스트 시나리오 설정 (Stages)
  stages: [
    { duration: '30s', target: 50 },   // Warm-up: 30초 동안 50명까지 증가
    { duration: '2m', target: 100 },    // Load: 2분 동안 100명 유지
    { duration: '1m', target: 200 },    // Stress: 1분 동안 200명까지 증가
    { duration: '1m', target: 100 },    // Recovery: 1분 동안 100명으로 감소
    { duration: '30s', target: 0 },     // Cool-down: 30초 동안 0명으로 감소
  ],

  // 임계치 설정
  thresholds: {
    // 95%의 요청이 500ms 이내에 응답
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    // 1% 미만의 요청만 실패 허용
    http_req_failed: ['rate<0.01'],
    // 에러율 10% 미만
    errors: ['rate<0.1'],
  },
};

// 테스트 데이터 (사전에 생성된 사용자 ID)
let userIds = [];
let boardIds = [];

/**
 * 테스트 데이터 초기화
 * 실제 테스트 전에 사용자와 게시판 데이터를 생성합니다.
 */
export function setup() {
  console.log('=== 테스트 데이터 생성 시작 ===');

  // 1. 사용자 데이터 생성 (10명)
  const userResponse = http.post(
    `${BASE_URL}/test-data/users?count=10`,
    null,
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (userResponse.status === 200) {
    const userData = JSON.parse(userResponse.body);
    userIds = userData.userIds || [];
    console.log(`생성된 사용자 수: ${userIds.length}`);
  } else {
    console.error('사용자 생성 실패:', userResponse.status);
    // 기존 사용자 조회 시도
    const existingUsers = http.get(`${BASE_URL}/user/list`);
    if (existingUsers.status === 200) {
      const users = JSON.parse(existingUsers.body);
      userIds = users.response?.map(u => u.id) || [];
      console.log(`기존 사용자 사용: ${userIds.length}명`);
    }
  }

  // 2. 게시판 데이터 생성 (각 사용자당 5개)
  if (userIds.length > 0) {
    const boardResponse = http.post(
      `${BASE_URL}/test-data/boards?count=${userIds.length * 5}&userId=${userIds[0]}`,
      null,
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (boardResponse.status === 200) {
      const boardData = JSON.parse(boardResponse.body);
      boardIds = boardData.boardIds || [];
      console.log(`생성된 게시판 수: ${boardIds.length}`);
    }
  }

  console.log('=== 테스트 데이터 생성 완료 ===');

  return {
    userIds: userIds,
    boardIds: boardIds,
  };
}

/**
 * 메인 테스트 함수
 */
export default function (data) {
  // 데이터가 없으면 기본값 사용
  const testUserIds = data?.userIds || [1, 2, 3, 4, 5];
  const testBoardIds = data?.boardIds || [];

  // 랜덤 사용자 선택
  const userId = testUserIds[Math.floor(Math.random() * testUserIds.length)];

  group('게시판 조회 시나리오', () => {
    // 1. 전체 게시판 목록 조회
    const listRes = http.get(`${BASE_URL}/board/all-list/${userId}`);

    const listCheck = check(listRes, {
      '게시판 목록 조회 성공': (r) => r.status === 200,
      '응답 시간 < 500ms': (r) => r.timings.duration < 500,
      '응답 본문 존재': (r) => r.body.length > 0,
    });

    requestCount.add(1);
    requestDuration.add(listRes.timings.duration);
    errorRate.add(listCheck === false);

    sleep(1);

    // 2. 특정 게시판 상세 조회
    if (testBoardIds.length > 0) {
      const boardId = testBoardIds[Math.floor(Math.random() * testBoardIds.length)];
      const detailRes = http.get(`${BASE_URL}/board/${boardId}`);

      check(detailRes, {
        '게시판 상세 조회 성공': (r) => r.status === 200,
        '응답 시간 < 500ms': (r) => r.timings.duration < 500,
      });

      requestCount.add(1);
      requestDuration.add(detailRes.timings.duration);
      errorRate.add(detailRes.status !== 200);
    }

    sleep(1);
  });

  group('사용자 조회 시나리오', () => {
    // 3. 사용자 프로필 조회
    const profileRes = http.get(`${BASE_URL}/user/profile/${userId}`);

    check(profileRes, {
      '사용자 프로필 조회 성공': (r) => r.status === 200,
      '응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });

    requestCount.add(1);
    requestDuration.add(profileRes.timings.duration);
    errorRate.add(profileRes.status !== 200);

    sleep(0.5);
  });

  // Think Time (실제 사용자 행동 시뮬레이션)
  sleep(Math.random() * 2 + 1); // 1~3초 랜덤 대기
}

/**
 * 테스트 종료 후 실행되는 함수
 */
export function teardown(data) {
  console.log('=== 부하 테스트 완료 ===');
  console.log(`테스트된 사용자 수: ${data?.userIds?.length || 0}`);
  console.log(`테스트된 게시판 수: ${data?.boardIds?.length || 0}`);
}
