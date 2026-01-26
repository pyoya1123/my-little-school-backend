# 프로파일링 가이드

## 개요
이 프로젝트는 Spring Boot Actuator와 Micrometer를 사용하여 성능 프로파일링을 지원합니다.

## 설정된 프로파일링 도구

### 1. Spring Boot Actuator
- **엔드포인트**: `http://localhost:8080/actuator`
- **주요 엔드포인트**:
  - `/actuator/health`: 애플리케이션 건강 상태
  - `/actuator/metrics`: 수집된 메트릭 목록
  - `/actuator/prometheus`: Prometheus 형식의 메트릭
  - `/actuator/httptrace`: HTTP 요청 추적 정보

### 2. Micrometer Prometheus
- **엔드포인트**: `http://localhost:8080/actuator/prometheus`
- Prometheus 형식으로 메트릭을 노출하여 Grafana 등으로 시각화 가능

### 3. Hibernate 통계
- Hibernate의 쿼리 실행 통계를 자동으로 수집
- 5분마다 로그에 통계 정보 출력
- 설정: `application.yml`의 `hibernate.generate_statistics: true`

## 수집되는 메트릭

### HTTP 메트릭
- `http.request.duration`: HTTP 요청 처리 시간
  - 태그: method, uri, status
- `api.response.duration`: API 엔드포인트 응답 시간

### 서비스 메트릭
- `service.method.duration`: 서비스 레이어 메서드 실행 시간
  - 태그: class, method

### Repository 메트릭
- `repository.method.duration`: Repository 메서드 실행 시간
  - 태그: class, method

### 데이터베이스 메트릭
- `database.query.duration`: 데이터베이스 쿼리 실행 시간
- Hibernate 통계:
  - 쿼리 실행 횟수 및 시간
  - 엔티티 로드/페치 횟수
  - 컬렉션 로드/페치 횟수
  - 세션 및 트랜잭션 통계

### JVM 메트릭
- 메모리 사용량
- GC 통계
- 스레드 통계
- 클래스 로딩 통계

## 사용 방법

### 1. 메트릭 조회
```bash
# 모든 메트릭 목록 조회
curl http://localhost:8080/actuator/metrics

# 특정 메트릭 조회 (예: HTTP 요청 시간)
curl http://localhost:8080/actuator/metrics/http.request.duration

# Prometheus 형식으로 조회
curl http://localhost:8080/actuator/prometheus
```

### 2. 느린 요청 모니터링
- 500ms 이상 걸리는 요청은 자동으로 WARN 레벨로 로깅됩니다.
- 로그에서 "Slow request detected" 메시지를 확인하세요.

### 3. Hibernate 통계 확인
- 애플리케이션 로그에서 5분마다 자동으로 출력되는 통계 정보 확인
- 또는 `HibernateStatisticsConfig`의 `logHibernateStatistics()` 메서드를 직접 호출

### 4. Prometheus + Grafana 연동
1. Prometheus 설정 파일에 다음 추가:
```yaml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

2. Grafana에서 Prometheus를 데이터 소스로 추가
3. 대시보드를 생성하여 메트릭 시각화

## 성능 병목 지점 파악 방법

### 1. HTTP 요청 시간 분석
```bash
# 평균 응답 시간 확인
curl http://localhost:8080/actuator/metrics/http.request.duration | jq

# 특정 엔드포인트의 응답 시간 확인
# (Prometheus 쿼리 사용)
```

### 2. 데이터베이스 쿼리 분석
- Hibernate 통계 로그에서 다음을 확인:
  - 총 쿼리 실행 횟수
  - 평균 쿼리 실행 시간
  - N+1 문제 가능성 (엔티티 페치 횟수 확인)

### 3. 서비스 메서드 분석
- `service.method.duration` 메트릭을 통해 느린 서비스 메서드 식별
- 태그를 통해 특정 클래스/메서드 필터링

### 4. Repository 메서드 분석
- `repository.method.duration` 메트릭을 통해 느린 쿼리 식별
- Hibernate 통계와 함께 분석하여 최적화 포인트 파악

## 주의사항

1. **프로덕션 환경**: 
   - `hibernate.generate_statistics`는 성능 오버헤드가 있으므로 프로덕션에서는 비활성화 권장
   - Actuator 엔드포인트는 보안 설정 필요

2. **메모리 사용량**:
   - 통계 수집으로 인한 메모리 사용량 증가 가능
   - 주기적으로 통계 리셋 고려

3. **로그 볼륨**:
   - 느린 요청 로깅이 많을 경우 로그 파일 크기 증가
   - 로그 로테이션 설정 권장

## 성능 최적화 체크리스트

- [ ] HTTP 요청 시간이 500ms 이상인 엔드포인트 확인
- [ ] 데이터베이스 쿼리 실행 횟수 확인 (N+1 문제)
- [ ] 평균 쿼리 실행 시간 확인
- [ ] 엔티티/컬렉션 페치 횟수 확인
- [ ] JVM 메모리 사용량 모니터링
- [ ] GC 통계 확인
- [ ] 느린 서비스/Repository 메서드 식별

## 참고 자료

- [Spring Boot Actuator 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer 공식 문서](https://micrometer.io/docs)
- [Hibernate 통계 문서](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#statistics)

