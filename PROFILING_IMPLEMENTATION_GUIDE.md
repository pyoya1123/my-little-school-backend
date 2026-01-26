# 프로파일링 시스템 구현 가이드

## 📋 목차
1. [개요](#개요)
2. [의존성 추가](#의존성-추가)
3. [설정 파일 변경](#설정-파일-변경)
4. [구현된 컴포넌트](#구현된-컴포넌트)
5. [사용 방법](#사용-방법)
6. [수집되는 메트릭](#수집되는-메트릭)
7. [성능 병목 지점 파악 방법](#성능-병목-지점-파악-방법)

---

## 개요

이 프로젝트에 **Spring Boot Actuator**와 **Micrometer**를 활용한 성능 프로파일링 시스템을 구현했습니다. 이를 통해 애플리케이션의 성능 병목 지점을 실시간으로 모니터링하고 분석할 수 있습니다.

### 구현 목적
- HTTP 요청 응답 시간 측정
- 데이터베이스 쿼리 성능 분석
- 서비스/Repository 레이어 메서드 실행 시간 측정
- Hibernate 통계 정보 수집
- 느린 요청 자동 감지 및 로깅

---

## 의존성 추가

### build.gradle 변경사항

```gradle
// Spring Boot Actuator (프로파일링 및 모니터링)
implementation 'org.springframework.boot:spring-boot-starter-actuator'

// Micrometer Prometheus (메트릭 수집)
implementation 'io.micrometer:micrometer-registry-prometheus'

// AOP (성능 측정용)
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

### 각 의존성 설명

#### 1. Spring Boot Actuator
- **역할**: 애플리케이션의 건강 상태, 메트릭, 환경 정보 등을 제공하는 엔드포인트 제공
- **주요 기능**:
  - `/actuator/health`: 애플리케이션 건강 상태
  - `/actuator/metrics`: 수집된 메트릭 목록
  - `/actuator/prometheus`: Prometheus 형식의 메트릭 노출
  - `/actuator/httptrace`: HTTP 요청 추적 정보

#### 2. Micrometer Prometheus
- **역할**: 애플리케이션 메트릭을 Prometheus 형식으로 노출
- **장점**: Grafana 등 시각화 도구와 연동 가능
- **사용**: `/actuator/prometheus` 엔드포인트를 통해 메트릭 수집

#### 3. Spring AOP
- **역할**: Aspect-Oriented Programming을 통한 횡단 관심사 처리
- **사용 목적**: 서비스/Repository 레이어의 모든 메서드 실행 시간을 자동으로 측정

---

## 설정 파일 변경

### application.yml 변경사항

#### 1. Hibernate 통계 활성화

```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
        # 프로파일링을 위한 Hibernate 통계 활성화
        generate_statistics: true
        # 쿼리 성능 로깅
        jdbc:
          batch_size: 20
        # 쿼리 실행 계획 캐싱
        query:
          plan_cache_max_size: 2048
```

**설명**:
- `generate_statistics: true`: Hibernate가 쿼리 실행 통계를 수집하도록 설정
- `batch_size: 20`: 배치 처리 크기 설정 (성능 최적화)
- `plan_cache_max_size: 2048`: 쿼리 실행 계획 캐시 크기 설정

#### 2. Spring Boot Actuator 설정

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,httptrace,env
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name:final_project}
      environment: ${spring.profiles.active:dev}
    # JVM 메트릭 수집
    jvm:
      enabled: true
    # HTTP 메트릭 수집
    http:
      server:
        requests:
          enabled: true
    # 데이터베이스 연결 풀 메트릭
    datasource:
      enabled: true
    # Hibernate 메트릭 수집
    hibernate:
      enabled: true
    # 시스템 메트릭 수집
    system:
      enabled: true
    # 프로세스 메트릭 수집
    process:
      enabled: true
```

**설명**:
- `exposure.include`: 노출할 엔드포인트 목록 지정
- `base-path`: Actuator 엔드포인트의 기본 경로 (`/actuator`)
- 각종 메트릭 수집 활성화: JVM, HTTP, 데이터베이스, Hibernate 등

#### 3. 로깅 레벨 설정

```yaml
logging:
  level:
    com.project.final_project.common.config.HibernateStatisticsConfig: INFO
    com.project.final_project.common.interceptor.PerformanceInterceptor: INFO
    com.project.final_project.common.aspect.PerformanceAspect: INFO
```

**설명**: 프로파일링 관련 컴포넌트의 로그 레벨을 INFO로 설정하여 성능 정보를 확인할 수 있도록 함

---

## 구현된 컴포넌트

### 1. MetricsConfig
**경로**: `src/main/java/com/project/final_project/common/config/MetricsConfig.java`

**역할**: Micrometer 메트릭 설정 및 커스텀 메트릭 정의

**주요 내용**:
```java
@Configuration
public class MetricsConfig {
    // @Timed 어노테이션 지원
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    // 데이터베이스 쿼리 성능 측정 Timer
    @Bean
    public Timer databaseQueryTimer(MeterRegistry registry) {
        return Timer.builder("database.query.duration")
            .description("데이터베이스 쿼리 실행 시간")
            .register(registry);
    }
    
    // API 엔드포인트 응답 시간 측정 Timer
    @Bean
    public Timer apiResponseTimer(MeterRegistry registry) {
        return Timer.builder("api.response.duration")
            .description("API 엔드포인트 응답 시간")
            .register(registry);
    }
}
```

**기능**:
- `TimedAspect`: `@Timed` 어노테이션을 사용하여 메서드 실행 시간 측정 가능
- `databaseQueryTimer`: 데이터베이스 쿼리 실행 시간 측정용 Timer
- `apiResponseTimer`: API 응답 시간 측정용 Timer

---

### 2. PerformanceInterceptor
**경로**: `src/main/java/com/project/final_project/common/interceptor/PerformanceInterceptor.java`

**역할**: HTTP 요청의 응답 시간을 측정하고 메트릭으로 수집

**작동 방식**:
1. `preHandle()`: 요청 시작 시점의 시간을 저장
2. `afterCompletion()`: 요청 완료 시점에 실행 시간을 계산하여 메트릭 수집

**주요 코드**:
```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    return true;
}

@Override
public void afterCompletion(...) {
    Long startTime = (Long) request.getAttribute("startTime");
    if (startTime != null) {
        long duration = System.currentTimeMillis() - startTime;
        
        // 메트릭 수집
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("http.request.duration")
            .tag("method", request.getMethod())
            .tag("uri", request.getRequestURI())
            .tag("status", String.valueOf(response.getStatus()))
            .register(meterRegistry));
        
        // 느린 요청 로깅 (500ms 이상)
        if (duration > 500) {
            log.warn("Slow request detected: {} {} took {}ms", 
                request.getMethod(), request.getRequestURI(), duration);
        }
    }
}
```

**기능**:
- 모든 HTTP 요청의 응답 시간 측정
- 메서드, URI, 상태 코드별로 태그를 달아 메트릭 수집
- 500ms 이상 걸리는 요청을 자동으로 WARN 레벨로 로깅

**수집 메트릭**: `http.request.duration` (태그: method, uri, status)

---

### 3. WebMvcConfig
**경로**: `src/main/java/com/project/final_project/common/config/WebMvcConfig.java`

**역할**: PerformanceInterceptor를 Spring MVC에 등록

**주요 코드**:
```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final PerformanceInterceptor performanceInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/actuator/**",
                "/swagger-ui/**",
                "/api-docs/**",
                "/swagger-ui-custom.html",
                "/error"
            );
    }
}
```

**기능**:
- 모든 경로(`/**`)에 인터셉터 적용
- Actuator, Swagger 등 모니터링 도구 경로는 제외하여 순환 참조 방지

---

### 4. PerformanceAspect
**경로**: `src/main/java/com/project/final_project/common/aspect/PerformanceAspect.java`

**역할**: AOP를 사용하여 서비스/Repository 레이어의 모든 메서드 실행 시간 측정

**주요 코드**:
```java
@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceAspect {
    private final MeterRegistry meterRegistry;
    
    // Service 레이어 측정
    @Around("execution(* com.project.final_project..service..*(..))")
    public Object measureServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("service.method.duration")
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry));
        }
    }
    
    // Repository 레이어 측정
    @Around("execution(* com.project.final_project..repository..*(..))")
    public Object measureRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 동일한 방식으로 Repository 메서드 측정
    }
}
```

**기능**:
- Service 레이어의 모든 메서드 실행 시간 자동 측정
- Repository 레이어의 모든 메서드 실행 시간 자동 측정
- 클래스명과 메서드명을 태그로 달아 상세 분석 가능

**수집 메트릭**:
- `service.method.duration` (태그: class, method)
- `repository.method.duration` (태그: class, method)

**장점**:
- 코드 수정 없이 모든 메서드의 성능 측정 가능
- 특정 메서드가 느린지 쉽게 파악 가능

---

### 5. HibernateStatisticsConfig
**경로**: `src/main/java/com/project/final_project/common/config/HibernateStatisticsConfig.java`

**역할**: Hibernate 통계 정보를 수집하고 주기적으로 로깅

**주요 코드**:
```java
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class HibernateStatisticsConfig {
    private final EntityManagerFactory entityManagerFactory;
    private Statistics statistics;
    
    @PostConstruct
    public void init() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
    }
    
    // 5분마다 통계 정보 로깅
    @Scheduled(fixedRate = 300000)
    public void logHibernateStatistics() {
        log.info("총 쿼리 실행 횟수: {}", statistics.getQueryExecutionCount());
        log.info("평균 쿼리 실행 시간: {}ms", ...);
        log.info("총 엔티티 로드 횟수: {}", statistics.getEntityLoadCount());
        // ... 기타 통계 정보
    }
    
    public void resetStatistics() {
        if (statistics != null) {
            statistics.clear();
        }
    }
}
```

**수집하는 통계 정보**:
- 쿼리 실행 횟수 및 시간
- 엔티티 로드/페치 횟수 (N+1 문제 감지에 유용)
- 컬렉션 로드/페치 횟수
- 세션 및 트랜잭션 통계
- 쿼리 플랜 캐시 히트/미스

**기능**:
- 애플리케이션 시작 시 Hibernate 통계 활성화
- 5분마다 자동으로 통계 정보를 로그에 출력
- 통계 리셋 기능 제공

---

### 6. ProfilingController
**경로**: `src/main/java/com/project/final_project/common/controller/ProfilingController.java`

**역할**: 실시간으로 프로파일링 정보를 조회할 수 있는 REST API 제공

**엔드포인트**:

#### GET `/profiling/hibernate-stats`
Hibernate 통계 정보를 JSON 형식으로 반환

**응답 예시**:
```json
{
  "queryExecutionCount": 150,
  "queryExecutionMaxTime": 250,
  "averageQueryExecutionTime": 1.67,
  "entityLoadCount": 500,
  "entityFetchCount": 20,
  "collectionLoadCount": 100,
  "collectionFetchCount": 15,
  "sessionOpenCount": 200,
  "transactionCount": 180,
  "successfulTransactionCount": 178,
  "queryPlanCacheHitCount": 1200,
  "queryPlanCacheMissCount": 50
}
```

**주요 필드 설명**:
- `queryExecutionCount`: 총 쿼리 실행 횟수
- `queryExecutionMaxTime`: 가장 느린 쿼리의 실행 시간 (ms)
- `averageQueryExecutionTime`: 평균 쿼리 실행 시간
- `entityFetchCount`: 엔티티 페치 횟수 (높으면 N+1 문제 가능성)
- `collectionFetchCount`: 컬렉션 페치 횟수 (높으면 N+1 문제 가능성)

#### GET `/profiling/hibernate-stats/reset`
Hibernate 통계 정보를 리셋

**응답**:
```json
{
  "message": "Hibernate 통계가 리셋되었습니다."
}
```

---

## 사용 방법

### 1. Actuator 엔드포인트 접근

애플리케이션 실행 후 다음 URL로 접근:

```bash
# Actuator 기본 엔드포인트 목록
http://localhost:8080/actuator

# 애플리케이션 건강 상태
http://localhost:8080/actuator/health

# 모든 메트릭 목록
http://localhost:8080/actuator/metrics

# 특정 메트릭 조회 (예: HTTP 요청 시간)
http://localhost:8080/actuator/metrics/http.request.duration

# Prometheus 형식 메트릭
http://localhost:8080/actuator/prometheus
```

### 2. Hibernate 통계 조회

```bash
# 실시간 통계 조회
curl http://localhost:8080/profiling/hibernate-stats

# 통계 리셋
curl http://localhost:8080/profiling/hibernate-stats/reset
```

### 3. 로그 확인

애플리케이션 로그에서 다음 정보 확인:
- **느린 요청**: 500ms 이상 걸리는 요청이 WARN 레벨로 로깅됨
- **Hibernate 통계**: 5분마다 자동으로 통계 정보 출력

---

## 수집되는 메트릭

### HTTP 메트릭

| 메트릭명 | 설명 | 태그 |
|---------|------|------|
| `http.request.duration` | HTTP 요청 처리 시간 | method, uri, status |

**예시**:
```
http_request_duration_seconds{method="GET",uri="/board/all-list/1",status="200"} 0.234
```

### 서비스 레이어 메트릭

| 메트릭명 | 설명 | 태그 |
|---------|------|------|
| `service.method.duration` | 서비스 메서드 실행 시간 | class, method |

**예시**:
```
service_method_duration_seconds{class="BoardService",method="getAllBoards"} 0.156
```

### Repository 레이어 메트릭

| 메트릭명 | 설명 | 태그 |
|---------|------|------|
| `repository.method.duration` | Repository 메서드 실행 시간 | class, method |

**예시**:
```
repository_method_duration_seconds{class="BoardRepository",method="findAll"} 0.089
```

### JVM 메트릭

- 메모리 사용량 (heap, non-heap)
- GC 통계 (횟수, 시간)
- 스레드 통계
- 클래스 로딩 통계

### 데이터베이스 메트릭

- 연결 풀 통계
- Hibernate 통계 (쿼리 실행 횟수, 시간 등)

---

## 성능 병목 지점 파악 방법

### 1. HTTP 요청 분석

**목적**: 어떤 API 엔드포인트가 느린지 파악

**방법**:
1. `/actuator/metrics/http.request.duration` 접근
2. URI별로 그룹화하여 평균 응답 시간 확인
3. 로그에서 "Slow request detected" 메시지 확인

**최적화 포인트**:
- 500ms 이상 걸리는 엔드포인트 식별
- 특정 URI 패턴이 느린지 확인

### 2. 데이터베이스 쿼리 분석

**목적**: N+1 문제, 느린 쿼리, 과도한 쿼리 실행 감지

**방법**:
1. `/profiling/hibernate-stats` 접근
2. 다음 지표 확인:
   - `queryExecutionCount`: 총 쿼리 실행 횟수
   - `averageQueryExecutionTime`: 평균 쿼리 실행 시간
   - `entityFetchCount`: 엔티티 페치 횟수 (높으면 N+1 문제 가능성)
   - `collectionFetchCount`: 컬렉션 페치 횟수 (높으면 N+1 문제 가능성)

**N+1 문제 감지**:
- `entityFetchCount` 또는 `collectionFetchCount`가 `entityLoadCount`보다 훨씬 높으면 N+1 문제 가능성
- 예: 게시판 100개 조회 시 쿼리가 100개 이상 실행되면 N+1 문제

**최적화 포인트**:
- JOIN FETCH 사용
- @EntityGraph 활용
- 배치 페치 크기 조정

### 3. 서비스/Repository 메서드 분석

**목적**: 특정 메서드가 성능 병목인지 파악

**방법**:
1. `/actuator/metrics/service.method.duration` 접근
2. `/actuator/metrics/repository.method.duration` 접근
3. 클래스명과 메서드명으로 필터링하여 느린 메서드 식별

**최적화 포인트**:
- 특정 서비스 메서드가 느리면 비즈니스 로직 최적화
- 특정 Repository 메서드가 느리면 쿼리 최적화

### 4. 종합 분석

**체크리스트**:
- [ ] HTTP 요청 시간이 500ms 이상인 엔드포인트 확인
- [ ] 데이터베이스 쿼리 실행 횟수 확인 (N+1 문제)
- [ ] 평균 쿼리 실행 시간 확인
- [ ] 엔티티/컬렉션 페치 횟수 확인
- [ ] JVM 메모리 사용량 모니터링
- [ ] GC 통계 확인
- [ ] 느린 서비스/Repository 메서드 식별

---

## 주의사항

### 1. 프로덕션 환경
- `hibernate.generate_statistics`는 성능 오버헤드가 있으므로 프로덕션에서는 비활성화 권장
- Actuator 엔드포인트는 보안 설정 필요 (인증/인가)

### 2. 메모리 사용량
- 통계 수집으로 인한 메모리 사용량 증가 가능
- 주기적으로 통계 리셋 고려 (`/profiling/hibernate-stats/reset`)

### 3. 로그 볼륨
- 느린 요청 로깅이 많을 경우 로그 파일 크기 증가
- 로그 로테이션 설정 권장

---

## 참고 자료

- [Spring Boot Actuator 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer 공식 문서](https://micrometer.io/docs)
- [Hibernate 통계 문서](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#statistics)

---

## 요약

이 프로파일링 시스템을 통해:
1. ✅ 모든 HTTP 요청의 응답 시간을 자동으로 측정
2. ✅ 서비스/Repository 레이어의 모든 메서드 실행 시간 측정
3. ✅ Hibernate 통계를 통한 데이터베이스 쿼리 성능 분석
4. ✅ 느린 요청 자동 감지 및 로깅
5. ✅ Prometheus 형식으로 메트릭 노출 (Grafana 연동 가능)
6. ✅ 실시간 통계 조회 API 제공

**다음 단계**: k6 부하 테스트를 실행하여 실제 성능 병목 지점을 파악하고 최적화하세요!

