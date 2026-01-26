import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  // 부하 테스트 시나리오 설정 (Stages)
  stages: [
    { duration: '10s', target: 10 }, // 처음 10초 동안 가상 유저(VUser)를 10명까지 늘림 (Ramp-up)
    { duration: '30s', target: 10 }, // 30초 동안 10명의 유저를 유지 (부하 지속)
    { duration: '10s', target: 0 },  // 마지막 10초 동안 유저를 0명으로 줄임 (Ramp-down)
  ],
  // 임계치 설정 (선택 사항)
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms(0.5초) 이내에 응답해야 성공으로 간주
  },
};

export default function () {
  // 1. 테스트할 API URL (로컬 서버 기준)
  // application-dev.yml 설정에 따라 포트를 5544로 변경
  const url = 'http://localhost:5544/board/all-list/1';

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 2. GET 요청 전송
  const res = http.get(url, params);

  // 3. 응답 검증 (Status Code가 200인지 확인)
  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  // 4. 휴식 시간 (Think Time)
  // 실제 유저는 요청 후 다음 요청까지 텀이 있습니다. 1초 대기.
  sleep(1);
}
