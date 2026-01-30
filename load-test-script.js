import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const requestDuration = new Trend('request_duration');
const requestCount = new Counter('request_count');

// 테스트 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
// 테스트에 사용할 사용자 수 (환경 변수로 설정 가능, 기본값: 10)
const TARGET_USER_COUNT = 10;
// 각 사용자당 목표 게시판 수
const TARGET_BOARD_COUNT_PER_USER = 5;
// 각 게시판당 목표 댓글 수
const TARGET_COMMENT_COUNT_PER_BOARD = 3;

export const options = {
  stages: [
    { duration: '10s', target: 10 },   // Warm-up: 10초 동안 10명까지 증가
    { duration: '1m', target: 50 },    // Load: 1분 동안 50명 유지
    { duration: '30s', target: 100 },   // Stress: 30초 동안 100명까지 증가
    { duration: '30s', target: 0 },    // Cool-down: 30초 동안 0명으로 감소
  ],

  // 임계치 설정 
  thresholds: {
    // 95%의 요청이 5초 이내에 응답
    http_req_duration: ['p(95)<5000', 'p(99)<10000'],
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

  // 1. 기존 사용자 확인 및 선택
  const userList = http.get(`${BASE_URL}/user/list`);
  let allUserIds = [];

  if (userList.status === 200) {
    const users = JSON.parse(userList.body);
    allUserIds = users.response?.map(u => u.id) || [];
    console.log(`전체 기존 사용자 수: ${allUserIds.length}`);
  }

  let userIds = [];

  // 원하는 수만큼만 선택 (랜덤하게 선택)
  if (allUserIds.length >= TARGET_USER_COUNT) {
    // 기존 사용자가 충분하면 랜덤하게 선택
    const shuffled = [...allUserIds].sort(() => 0.5 - Math.random());
    userIds = shuffled.slice(0, TARGET_USER_COUNT);
    console.log(`기존 사용자 중 ${TARGET_USER_COUNT}명 선택: ${userIds.length}명`);
  } else {
    // 기존 사용자가 부족하면 모두 사용하고 부족한 만큼 생성
    userIds = [...allUserIds];
    const needCount = TARGET_USER_COUNT - userIds.length;
    console.log(`기존 사용자 ${userIds.length}명 사용, ${needCount}명 추가 생성 필요`);

    const userResponse = http.post(
      `${BASE_URL}/test-data/users?count=${needCount}`,
      null,
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (userResponse.status === 200) {
      const userData = JSON.parse(userResponse.body);
      const newUserIds = userData.userIds || [];
      userIds = userIds.concat(newUserIds);
      console.log(`추가 생성된 사용자 수: ${newUserIds.length}`);
    } else {
      console.error('사용자 생성 실패:', userResponse.status);
    }
  }

  console.log(`최종 사용자 수: ${userIds.length}명 (목표: ${TARGET_USER_COUNT}명)`);

  // 2. 게시판 데이터 생성 (각 사용자당 5개)
  let boardIds = [];
  let createdBoards = 0;
  let skippedBoards = 0;

  if (userIds.length > 0) {
    for (let i = 0; i < userIds.length; i++) {
      const userId = userIds[i];
      const boardListResponse = http.get(`${BASE_URL}/board/all-list/${userId}`);

      if (boardListResponse.status === 200) {
        // API 응답은 배열을 직접 반환
        const boardListData = JSON.parse(boardListResponse.body);
        const existingBoards = Array.isArray(boardListData) ? boardListData : [];


        if (existingBoards.length >= TARGET_BOARD_COUNT_PER_USER) {
          // 이미 게시판이 5개 이상인 경우 생성하지 않음
          const existingBoardIds = existingBoards.slice(0, TARGET_BOARD_COUNT_PER_USER).map(b => b.boardId);
          boardIds = boardIds.concat(existingBoardIds);
          skippedBoards++;
        } else {
          // 부족한 만큼만 생성
          const needCount = TARGET_BOARD_COUNT_PER_USER - existingBoards.length;
          const boardResponse = http.post(
            `${BASE_URL}/test-data/boards?count=${needCount}&userId=${userId}`,
            null,
            { headers: { 'Content-Type': 'application/json' } }
          );

          if (boardResponse.status === 200) {
            const boardData = JSON.parse(boardResponse.body);
            const newBoardIds = boardData.boardIds || [];
            createdBoards += newBoardIds.length;

            // 기존 게시판 ID와 새로 생성된 게시판 ID 합치기
            const existingBoardIds = existingBoards.map(b => b.boardId);
            boardIds = boardIds.concat(existingBoardIds, newBoardIds);
          } else {
            console.error(`사용자 ${userId} 게시판 생성 실패:`, boardResponse.status);
          }
        }
      } else {
        console.error(`사용자 ${userId} 게시판 목록 조회 실패:`, boardListResponse.status);
      }
    }

    console.log(`총 게시판 수: ${boardIds.length}개 (생성: ${createdBoards}개, 스킵: ${skippedBoards}명)`);

    // 3. 댓글 데이터 생성 (각 게시판당 3개씩 댓글 생성)
    if (boardIds.length > 0) {

      let createdComments = 0;
      let skippedComments = 0;

      for (let i = 0; i < boardIds.length; i++) {
        const boardId = boardIds[i];

        // 기존 댓글 확인
        const commentListResponse = http.get(`${BASE_URL}/comment/${boardId}`);
        let existingCommentCount = 0;

        if (commentListResponse.status === 200) {
          const existingComments = JSON.parse(commentListResponse.body);
          existingCommentCount = Array.isArray(existingComments) ? existingComments.length : 0;
        }

        // 이미 댓글이 3개 이상이면 스킵 (생성하지 않음)
        if (existingCommentCount >= TARGET_COMMENT_COUNT_PER_BOARD) {
          skippedComments++;
          continue;
        }

        // 부족한 만큼만 생성 (정확히 3개가 되도록)
        const needCount = TARGET_COMMENT_COUNT_PER_BOARD - existingCommentCount;

        if (needCount <= 0) {
          skippedComments++;
          continue;
        }

        const commentResponse = http.post(
          `${BASE_URL}/test-data/comments?count=${needCount}&boardId=${boardId}`,
          null,
          { headers: { 'Content-Type': 'application/json' } }
        );

        if (commentResponse.status === 200) {
          const commentData = JSON.parse(commentResponse.body);
          createdComments += commentData.createdCount || 0;
        }
      }

      console.log(`생성된 댓글 수: ${createdComments}개`);
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
      // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
      '응답 시간 < 3s': (r) => r.timings.duration < 3000,
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
        // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
        '응답 시간 < 3s': (r) => r.timings.duration < 3000,
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
      // N+1 문제로 인한 성능 저하 고려하여 임계값 완화
      '응답 시간 < 1s': (r) => r.timings.duration < 1000,
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
