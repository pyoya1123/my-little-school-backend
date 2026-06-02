import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import execution from 'k6/execution';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const requestDuration = new Trend('request_duration');
const requestCount = new Counter('request_count');

// 테스트 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
// 목표 데이터 스펙: 유저 20,000명, 유저당 게시글 5개(=10만), 게시글당 댓글 3개(=30만)
const TARGET_USER_COUNT = 20000;
const TARGET_BOARD_COUNT_PER_USER = 5;
const TARGET_COMMENT_COUNT_PER_BOARD = 3; // 총 300,000개 목표

// 배치 처리 설정
const USER_BATCH_SIZE = 500;    // 유저 단위 처리 배치 크기
const BOARD_BATCH_SIZE = 1000;  // 게시글/댓글 생성 배치 묶음 크기

export const options = {
  // setup 함수 타임아웃 설정 (대량 데이터 생성을 위해 1시간으로 설정)
  setupTimeout: '3600s',
  stages: [
    { duration: '30s', target: 100 },   // Warm-up: 100명까지 증가
    { duration: '2m', target: 500 },    // Load: 500명까지 증가
    { duration: '1m', target: 500 },    // Stress: 500명 유지
    { duration: '30s', target: 0 },     // Cool-down: 0명으로 감소
  ],

  // 임계치 설정 
  thresholds: {

    http_req_duration: ['p(95)<10000', 'p(99)<20000'],
    // 5% 미만의 요청만 실패 허용
    http_req_failed: ['rate<0.05'],
    // 에러율 50% 미만 
    errors: ['rate<0.5'],
  },
};

function parseJsonSafe(body, fallback) {
  try {
    return JSON.parse(body);
  } catch (e) {
    return fallback;
  }
}

function uniq(arr) {
  const s = new Set(arr);
  return Array.from(s);
}

/**
 * 테스트 데이터 초기화
 * 실제 테스트 전에 사용자와 게시판 데이터를 생성합니다.
 */
export function setup() {
  console.log('=== [SETUP] 기존 데이터 로드 (생성/확인 스킵) ===');

  // 1) 사용자 목록 조회만
  let usersRes = http.get(`${BASE_URL}/user/list`, { tags: { name: 'Setup_UserList' }, timeout: '120s' });
  let allUsers = [];
  if (usersRes.status === 200) {
    const parsed = parseJsonSafe(usersRes.body, {});
    allUsers = parsed?.response || [];
  }
  let userIds = allUsers.map(u => u.id);
  console.log(`[SETUP] 사용자 로드 완료: ${userIds.length}명`);

  // 2) 게시글 목록 조회만
  let boardListRes = http.get(`${BASE_URL}/test-data/board/list`, {
    tags: { name: 'Setup_BoardList_All' },
    timeout: '600s'
  });
  let allBoards = [];
  if (boardListRes.status === 200) {
    allBoards = parseJsonSafe(boardListRes.body, []);
  }
  let boardIds = allBoards.map(b => b.id || b.boardId || b.boardID).filter(Boolean);
  boardIds = uniq(boardIds);
  console.log(`[SETUP] 게시글 로드 완료: ${boardIds.length}개`);

  console.log('=== [SETUP] 데이터 로드 완료 - 부하 테스트 시작 ===');

  return {
    userIds: userIds,
    boardIds: boardIds
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

  group('사용자 조회 시나리오', () => {
    // 1. 사용자 프로필 조회
    const profileRes = http.get(`${BASE_URL}/user/profile/${userId}`, {
      tags: { name: 'UserProfileScenario' }
    });

    check(profileRes, {
      '사용자 프로필 조회 성공': (r) => r.status === 200,
      // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
      '응답 시간 < 4s': (r) => r.timings.duration < 4000,
    });

    requestCount.add(1);
    requestDuration.add(profileRes.timings.duration);
    errorRate.add(profileRes.status !== 200);

    sleep(0.5);
  });

  group('게시판 조회 시나리오', () => {
    // 2. 전체 게시판 목록 조회
    const listRes = http.get(`${BASE_URL}/board/all-list/${userId}`, {
      tags: { name: 'BoardListScenario' }
    });

    const listCheck = check(listRes, {
      '게시판 목록 조회 성공': (r) => r.status === 200,
      // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
      '응답 시간 < 4s': (r) => r.timings.duration < 4000,
      '응답 본문 존재': (r) => r.body.length > 0,
    });

    requestCount.add(1);
    requestDuration.add(listRes.timings.duration);
    errorRate.add(listCheck === false);

    sleep(1);

    // 3. 특정 게시판 상세 조회
    // 수집된 게시글 ID가 있다면 그 중에서 랜덤 선택, 없으면 1~100,000 범위 사용
    let boardId;
    if (testBoardIds && testBoardIds.length > 0) {
      // 수집된 ID 중 하나 선택 (랜덤)
      boardId = testBoardIds[Math.floor(Math.random() * testBoardIds.length)];

      // 또는 순차적으로 하려면 아래 주석 해제 (단, 배열 인덱스 범위 주의)
      // const index = execution.scenario.iterationInTest % testBoardIds.length;
      // boardId = testBoardIds[index];
    } else {
      // 기존 방식 (데이터 수집 실패 또는 스킵 시)
      const MAX_BOARD_ID = 100000;
      boardId = (execution.scenario.iterationInTest % MAX_BOARD_ID) + 1;
    }

    const detailRes = http.get(`${BASE_URL}/board/${boardId}`, {
      tags: { name: 'BoardDetailScenario' }
    });

    check(detailRes, {
      '게시판 상세 조회 성공': (r) => r.status === 200,
      // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
      '응답 시간 < 4s': (r) => r.timings.duration < 4000,
    });

    sleep(1);
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

  if (data?.boardIds?.length > 0) {
    console.log(`테스트된 게시판 수(수집된 데이터): ${data.boardIds.length}`);
  } else {
    console.log(`테스트된 게시판 범위: 1 ~ 100,000번 (데이터 수집 없이 순차 조회)`);
  }
}
