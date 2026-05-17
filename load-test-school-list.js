import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PROFILE = __ENV.TEST_PROFILE || '100'; // '100' 또는 '500'

const TARGET_VUS = Number(TEST_PROFILE) === 500 ? 500 : 100;
const STEADY_DURATION = __ENV.STEADY_DURATION || '2m';

const RESET_STATS_BEFORE_TEST =
    (__ENV.RESET_STATS_BEFORE_TEST || 'true').toLowerCase() === 'true';

const THINK_TIME_MIN = Number(__ENV.THINK_TIME_MIN || '0.3');
const THINK_TIME_MAX = Number(__ENV.THINK_TIME_MAX || '1.2');

const P95_THRESHOLD_MS = Number(
    __ENV.P95_THRESHOLD_MS || (TARGET_VUS === 100 ? '300' : '1200')
);

const schoolListErrors = new Rate('school_list_errors');
const schoolListDuration = new Trend('school_list_duration_ms');
const schoolListRequestCount = new Counter('school_list_request_count');


function buildStages(vus) {
    return [
        { duration: '30s', target: Math.max(1, Math.floor(vus * 0.3))},
        { duration: '1m', target: vus },
        { duration: STEADY_DURATION, target: vus },
        { duration: '30s', target: 0 },
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

    thresholds: {
        'http_req_duration{name:SchoolList}': [`p(95)<${P95_THRESHOLD_MS}`],
        'http_req_failed{name:SchoolList}': ['rate<0.01'],
        school_list_errors: ['rate<0.01'],
    },
};


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


function looksLikeSchoolListPayload(bodyText) {
    const parsed = parseJsonSafe(bodyText, null);
    if(!parsed) return false;

    if(parsed.success !== true) return false;
    if(!Array.isArray(parsed.response)) return false;
    if(parsed.error !== null) return false;

    return parsed.response.every((school) =>
        school &&
        typeof school === 'object' &&
        Number.isInteger(school.id) &&
        typeof school.schoolName === 'string' &&
        (school.location === null || typeof school.location === 'string') &&
        (school.userList === null || Array.isArray(school.userList))
    );
}


function resetHibernateStats() {
    const res = http.get(`${BASE_URL}/profiling/hibernate-stats/reset`, {
        tags: {name: 'HibernateStatsReset'},
        timeout: '30s',
    })

    check(res, {
        'hibernate reset status is 200': (r) => r.status === 200,
    });
}


function fetchHibernateStats() {
    const res = http.get(`${BASE_URL}/profiling/hibernate-stats`, {
        tags: {name: 'HibernateStatsRead'},
        timeout: '30s',
    });

    return  {
        status: res.status,
        body: parseJsonSafe(res.body, {}),
    };
}


function fetchHibernateQueryStats() {
    const res = http.get(`${BASE_URL}/profiling/hibernate-stats/queries`, {
        tags: { name: 'HibernateQueryStatsRead' },
        timeout: '30s',
    });

    return  {
        status: res.status,
        body: parseJsonSafe(res.body, {}),
    };
}


export function setup() {
    console.log(`\n[setup] BASE_URL=${BASE_URL}, TARGET_VUS=${TARGET_VUS}`);

    if(RESET_STATS_BEFORE_TEST) {
        resetHibernateStats();
        console.log('[setup] Hibernate statistics reset completed');
    } else {
        console.log('[setup] Hibernate statistics reset skipped');
    }

    return {
        startedAt: new Date().toISOString(),
        baseUrl: BASE_URL,
        targetVus: TARGET_VUS,
    };
}

export default function() {
    group('GET /school/list scenario', () => {
        const headers = {
            Accept: 'application/json',
        };
        if (__ENV.ACCESS_TOKEN) {
            headers.Authorization = `Bearer ${__ENV.ACCESS_TOKEN}`;
        }

        const res = http.get(`${BASE_URL}/school/list`, {
            tags: { name: 'SchoolList'},
            timeout: '60s',
            headers,
        })

        const functionalOk = check(res, {
            'status is 200': (r) => r.status === 200,
            'body is not empty': (r) => !!r.body && r.body.length > 0,
            'payload shape looks valid': (r) => looksLikeSchoolListPayload(r.body),
        });

        check(res, {
            'response time < 2000ms': (r) => r.timings.duration < 2000,
        });

        schoolListRequestCount.add(1);
        schoolListDuration.add(res.timings.duration);
        schoolListErrors.add(!functionalOk);

        sleepBetween(THINK_TIME_MIN, THINK_TIME_MAX);
    });
}

export function teardown(data) {
    console.log(`\n[teardown] startedAt=${data.startedAt}, targetVus=${data.targetVus}`);

    const totalStats = fetchHibernateStats();
    const queryStats = fetchHibernateQueryStats();

    console.log(`\n[teardown] /profiling/hibernate-stats status=${totalStats.status}`);
    console.log(JSON.stringify(totalStats.body, null, 2));

    console.log(`\n[teardown] /profiling/hibernate-stats/queries status=${queryStats.status}`);
    console.log(JSON.stringify(queryStats.body, null, 2));
    const queryExecutionCount = Number(totalStats.body?.queryExecutionCount ?? 0);

    if (!Number.isFinite(queryExecutionCount)) {
        console.log('[teardown] queryExecutionCount 파싱 실패');
        return;
    }

    console.log(`[teardown] queryExecutionCount=${queryExecutionCount}`);
    console.log(
        '[teardown] avgQueriesPerRequest = queryExecutionCount / school_list_request_count ' +
        '(school_list_request_count는 k6 최종 summary에서 확인)'
    );
}
