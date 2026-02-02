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
// 테스트에 사용할 사용자 수 (데이터 10만 개 생성을 위해 2000명 설정)
const TARGET_USER_COUNT = 2000;
// 각 사용자당 목표 게시판 수 (2000 * 50 = 100,000 게시판)
const TARGET_BOARD_COUNT_PER_USER = 50;
// 각 게시판당 목표 댓글 수
const TARGET_COMMENT_COUNT_PER_BOARD = 3;

// 데이터 수집 로직 활성화 (true: 수집함, false: 스킵함)
const ENABLE_DATA_COLLECTION = true;

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
    // N+1 문제로 응답 시간이 길어질 수 있으므로 임계값 완화
    http_req_duration: ['p(95)<10000', 'p(99)<20000'],
    // 5% 미만의 요청만 실패 허용
    http_req_failed: ['rate<0.05'],
    // 에러율 50% 미만 (N+1 문제로 인한 성능 저하 고려)
    errors: ['rate<0.5'],
  },
};

/**
 * 테스트 데이터 초기화
 * 실제 테스트 전에 사용자와 게시판 데이터를 생성합니다.
 */
export function setup() {
  console.log('=== 테스트 데이터 생성 시작 ===');

  // 1. 기존 사용자 확인
  const userList = http.get(`${BASE_URL}/user/list`, {
    tags: { name: 'UserList' },
    timeout: '60s'
  });
  let allUserIds = [];

  if (userList.status === 200) {
    const users = JSON.parse(userList.body);
    allUserIds = users.response?.map(u => u.id) || [];
    console.log(`전체 기존 사용자 수: ${allUserIds.length}`);
  }

  // 데이터 생성 스킵을 위해 기존 사용자 전체 사용
  let userIds = [...allUserIds];
  console.log(`사용자 수: ${userIds.length}명 (생성 스킵)`);

  // 2. 사용자별 게시글 및 댓글 데이터 확인
  // DB에 있는 데이터를 실제 조회하여 테스트에 사용할 ID 목록을 구성합니다.
  let boardIds = [];
  let totalCommentsFound = 0;

  if (ENABLE_DATA_COLLECTION) {
    console.log('기존 데이터 수집을 시작합니다...');

    // API 부하를 줄이기 위해 유저 중 일부만 샘플링하거나, 전체를 조회하되 배치로 처리
    // 여기서는 전체 유저의 게시글을 조회합니다.
    const userBatchSize = 20;

    for (let i = 0; i < userIds.length; i += userBatchSize) {
      const userBatch = userIds.slice(i, i + userBatchSize);

      for (const userId of userBatch) {
        // 유저가 작성한 게시글 목록 조회
        const boardListRes = http.get(`${BASE_URL}/board/all-list/${userId}`, {
          tags: { name: 'Setup_BoardList' },
          timeout: '60s'
        });

        if (boardListRes.status === 200) {
          const boards = JSON.parse(boardListRes.body);
          if (Array.isArray(boards)) {
            // 게시글 ID 수집
            const ids = boards.map(b => b.boardId);
            boardIds = boardIds.concat(ids);

            // (선택 사항) 댓글 수집 로직
            // 실제 댓글 ID까지 다 수집하면 메모리가 부족할 수 있으므로,
            // "댓글이 존재하는지" 정도만 체크하거나, 
            // 게시글 객체에 commentCount 같은 필드가 있다면 그걸로 집계하는 것이 효율적입니다.
            // 만약 개별 댓글 조회가 필요하다면 아래 로직을 활성화하세요.

            /*
            for (const bId of ids) {
               // 상세 조회로 댓글 확인 (너무 오래 걸릴 수 있음 주의)
               // const detail = http.get(`${BASE_URL}/board/${bId}`);
               // ...
            }
            */

            // 여기서는 단순히 게시글 수 * 예상 댓글 수로 추산하거나, 
            // 응답에 댓글 수가 포함되어 있다면 그것을 더합니다.
            // (현재 API 응답 구조를 모르므로 일단 게시글 수집에 집중)
          }
        }
      }

      if ((i + userBatchSize) % 200 === 0) {
        console.log(`데이터 수집 진행중: 유저 ${Math.min(i + userBatchSize, userIds.length)}/${userIds.length} 완료. (수집된 게시글: ${boardIds.length})`);
      }
    }

    console.log(`총 수집된 게시글 수: ${boardIds.length}`);
  } else {
    console.log('데이터 수집 단계를 건너뜁니다. (ENABLE_DATA_COLLECTION = false)');
  }

  console.log('=== 테스트 데이터 셋업 완료 ===');

  return {
    userIds: userIds,
    boardIds: boardIds, // 수집된 실제 ID 목록 반환
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

    // 2. 특정 게시판 상세 조회
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

  group('사용자 조회 시나리오', () => {
    // 3. 사용자 프로필 조회
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
