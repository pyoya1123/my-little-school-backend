import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

/**
 * ================================================================
 * load-test-script-school-list-guide.js
 * ================================================================
 * 목적
 * - /school/list API를 대상으로 부하 테스트를 연습하기 위한 "가이드용" 스크립트
 * - VU 100 / VU 500 두 프로필을 같은 코드에서 선택 실행
 * - 테스트 전/후 Hibernate Statistics를 조회해 쿼리 지표를 같이 확인
 *
 * 중요한 의도
 * - 이 파일은 "완성본 정답"보다 "학습용 템플릿"에 가깝다.
 * - 주석을 읽고, TODO 지점을 직접 수정하면서 자신의 스크립트로 완성하면 된다.
 */

// -----------------------------------------------------------------
// 1) 환경 변수 입력 구간
// -----------------------------------------------------------------
// __ENV는 k6가 실행 시 주입하는 환경 변수 객체다.
// 예: TEST_PROFILE=500 BASE_URL=http://localhost:8080 k6 run ...

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PROFILE = __ENV.TEST_PROFILE || '100'; // '100' 또는 '500'

// 문자열 -> 숫자 변환. Number('500') = 500
const TARGET_VUS = Number(TEST_PROFILE) === 500 ? 500 : 100;

// 유지 부하 구간. 실제 비교는 steady 구간 값을 주로 본다.
const STEADY_DURATION = __ENV.STEADY_DURATION || '2m';

// Hibernate 통계 초기화 여부
const RESET_STATS_BEFORE_TEST =
  (__ENV.RESET_STATS_BEFORE_TEST || 'true').toLowerCase() === 'true';

// 사용자 생각 시간(think time). 0이면 순수 API 최대 처리량 측정에 가까워진다.
const THINK_TIME_MIN = Number(__ENV.THINK_TIME_MIN || '0.3');
const THINK_TIME_MAX = Number(__ENV.THINK_TIME_MAX || '1.2');

// 목표 성능 기준(Threshold). 필요 시 팀 SLA에 맞게 바꿔서 사용.
const P95_THRESHOLD_MS = Number(
  __ENV.P95_THRESHOLD_MS || (TARGET_VUS === 100 ? '300' : '1200')
);

// -----------------------------------------------------------------
// 2) 커스텀 메트릭 정의
// -----------------------------------------------------------------
// Rate: 성공/실패 비율 측정 (0~1)
// Trend: 응답시간 분포/평균/백분위 측정
// Counter: 누적 횟수 측정

const schoolListErrors = new Rate('school_list_errors');
const schoolListDuration = new Trend('school_list_duration_ms');
const schoolListRequestCount = new Counter('school_list_request_count');

// -----------------------------------------------------------------
// 3) 실행 패턴(options) 정의
// -----------------------------------------------------------------
// ramping-vus: VU를 단계적으로 올리고/유지하고/내리는 대표 패턴
// stages의 target은 "동시 사용자 수"를 의미

function buildStages(vus) {
  return [
    { duration: '30s', target: Math.max(1, Math.floor(vus * 0.3)) }, // 워밍업
    { duration: '1m', target: vus }, // 상승
    { duration: STEADY_DURATION, target: vus }, // 유지(주 관찰 구간)
    { duration: '30s', target: 0 }, // 정리
  ];
}

export const options = {
  setupTimeout: '120s',
  teardownTimeout: '120s',

  scenarios: {
    school_list_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: buildStages(TARGET_VUS),
      gracefulRampDown: '10s',
    },
  },

  // 핵심 포인트:
  // setup/teardown에서 호출하는 /profiling API가 전체 지표를 오염시키지 않도록
  // name 태그가 SchoolList인 요청만 기준으로 threshold를 둔다.
  thresholds: {
    'http_req_duration{name:SchoolList}': [`p(95)<${P95_THRESHOLD_MS}`],
    'http_req_failed{name:SchoolList}': ['rate<0.01'],
    school_list_errors: ['rate<0.01'],
  },
};

// -----------------------------------------------------------------
// 4) 유틸 함수
// -----------------------------------------------------------------

function parseJsonSafe(text, fallback) {
  try {
    return JSON.parse(text);
  } catch (e) {
    return fallback;
  }
}

function sleepBetween(minSec, maxSec) {
  const delta = Math.max(0, maxSec - minSec);
  sleep(minSec + Math.random() * delta);
}

// /school/list 응답이 "정상 구조인지" 확인하는 최소 검증 함수.
// 프로젝트의 실제 응답 스키마에 맞게 직접 보강하는 것이 학습 포인트다.
function looksLikeSchoolListPayload(bodyText) {
  const parsed = parseJsonSafe(bodyText, null);
  if (!parsed) return false;

  // TODO:
  // 현재 프로젝트의 success wrapper 구조에 맞춰 조건을 더 구체화해보세요.
  // 예) parsed.success === true && Array.isArray(parsed.response)

  if (Array.isArray(parsed)) return true;
  if (Array.isArray(parsed.response)) return true;
  if (Array.isArray(parsed.data)) return true;

  return false;
}

// -----------------------------------------------------------------
// 5) Hibernate Statistics 보조 함수
// -----------------------------------------------------------------
// 현재 프로젝트에는 다음 엔드포인트가 이미 구현되어 있다.
// - GET /profiling/hibernate-stats/reset
// - GET /profiling/hibernate-stats
// - GET /profiling/hibernate-stats/queries

function resetHibernateStats() {
  const res = http.get(`${BASE_URL}/profiling/hibernate-stats/reset`, {
    tags: { name: 'HibernateStatsReset' },
    timeout: '30s',
  });

  // reset 실패를 테스트 중단 조건으로 볼지, 경고로 볼지는 팀 정책에 따라 선택
  check(res, {
    'hibernate reset status is 200': (r) => r.status === 200,
  });
}

function fetchHibernateStats() {
  const res = http.get(`${BASE_URL}/profiling/hibernate-stats`, {
    tags: { name: 'HibernateStatsRead' },
    timeout: '30s',
  });

  return {
    status: res.status,
    body: parseJsonSafe(res.body, {}),
  };
}

function fetchHibernateQueryStats() {
  const res = http.get(`${BASE_URL}/profiling/hibernate-stats/queries`, {
    tags: { name: 'HibernateQueryStatsRead' },
    timeout: '30s',
  });

  return {
    status: res.status,
    body: parseJsonSafe(res.body, {}),
  };
}

// -----------------------------------------------------------------
// 6) setup: 테스트 시작 전에 1회 실행
// -----------------------------------------------------------------
// 여기서는 부하를 걸기 전에 통계를 초기화해서
// "이번 테스트에서 발생한 쿼리"만 보기 좋게 만든다.

export function setup() {
  console.log(`\n[setup] BASE_URL=${BASE_URL}, TARGET_VUS=${TARGET_VUS}`);

  if (RESET_STATS_BEFORE_TEST) {
    resetHibernateStats();
    console.log('[setup] Hibernate statistics reset completed');
  } else {
    console.log('[setup] Hibernate statistics reset skipped');
  }

  // setup에서 반환한 값은 default/teardown에서 data로 받을 수 있다.
  return {
    startedAt: new Date().toISOString(),
    baseUrl: BASE_URL,
    targetVus: TARGET_VUS,
  };
}

// -----------------------------------------------------------------
// 7) default: 각 VU가 반복 실행하는 본문
// -----------------------------------------------------------------
// 이 함수가 실제 사용자 트래픽을 흉내낸다.

export default function () {
  group('GET /school/list scenario', () => {
    const res = http.get(`${BASE_URL}/school/list`, {
      tags: { name: 'SchoolList' },
      timeout: '60s',
      headers: {
        Accept: 'application/json',
        // TODO: 인증이 필요한 환경이면 Authorization 헤더를 추가
        // Authorization: `Bearer ${__ENV.ACCESS_TOKEN}`,
      },
    });

    const ok = check(res, {
      'status is 200': (r) => r.status === 200,
      'body is not empty': (r) => !!r.body && r.body.length > 0,
      'payload shape looks valid': (r) => looksLikeSchoolListPayload(r.body),
      // TODO: 목표치를 직접 정해보세요.
      // 처음엔 느슨하게 시작하고(예: 2000ms), 안정화되면 점점 조이기
      'response time < 2000ms': (r) => r.timings.duration < 2000,
    });

    schoolListRequestCount.add(1);
    schoolListDuration.add(res.timings.duration);
    schoolListErrors.add(!ok);

    // 너무 공격적인 무간격 호출을 피하고 현실적인 사용자 간격을 일부 반영
    sleepBetween(THINK_TIME_MIN, THINK_TIME_MAX);
  });
}

// -----------------------------------------------------------------
// 8) teardown: 테스트 종료 후 1회 실행
// -----------------------------------------------------------------
// 부하테스트가 끝난 뒤 Hibernate 통계를 읽어서 콘솔에 남긴다.

export function teardown(data) {
  console.log(`\n[teardown] startedAt=${data.startedAt}, targetVus=${data.targetVus}`);

  const totalStats = fetchHibernateStats();
  const queryStats = fetchHibernateQueryStats();

  console.log(`\n[teardown] /profiling/hibernate-stats status=${totalStats.status}`);
  console.log(JSON.stringify(totalStats.body, null, 2));

  console.log(`\n[teardown] /profiling/hibernate-stats/queries status=${queryStats.status}`);
  console.log(JSON.stringify(queryStats.body, null, 2));

  // TODO:
  // 여기서 queryExecutionCount를 보고 "요청 1건당 평균 쿼리 수"를 계산해보세요.
  // 힌트: 테스트 전 reset을 했으므로 queryExecutionCount가 이번 런의 총 쿼리와 거의 유사.
  //      평균 쿼리 수 = queryExecutionCount / school_list_request_count(요청 수)
  //      요청 수는 k6 최종 요약의 custom counter 값을 참고하면 된다.
}

// -----------------------------------------------------------------
// 9) (선택) 결과 파일 저장
// -----------------------------------------------------------------
// handleSummary를 구현하면 k6 결과를 파일로 남길 수 있다.
// 아래는 "활성화 예시"이며, 지금은 주석 처리 상태로 둔다.

// export function handleSummary(summary) {
//   const profile = TARGET_VUS === 500 ? 'vu500' : 'vu100';
//   return {
//     [`./k6-summary-school-list-${profile}.json`]: JSON.stringify(summary, null, 2),
//   };
// }
