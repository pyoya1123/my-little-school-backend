package com.project.final_project.common.controller;

import com.project.final_project.common.config.HibernateStatisticsConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityManagerFactory;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.stat.QueryStatistics;

/**
 * 프로파일링 정보를 조회하는 컨트롤러
 * 실시간으로 성능 통계를 확인할 수 있습니다.
 */
@RestController
@RequestMapping("/profiling")
@RequiredArgsConstructor
public class ProfilingController {

  private final EntityManagerFactory entityManagerFactory;
  private final HibernateStatisticsConfig hibernateStatisticsConfig;
  private final MeterRegistry meterRegistry;

  /**
   * Hibernate 통계 정보를 JSON 형식으로 반환합니다.
   */
  @GetMapping("/hibernate-stats")
  public ResponseEntity<Map<String, Object>> getHibernateStatistics() {
    try {
      SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
      Statistics statistics = sessionFactory.getStatistics();

      if (statistics == null || !statistics.isStatisticsEnabled()) {
        return ResponseEntity.ok(Map.of("error", "Hibernate 통계가 활성화되지 않았습니다."));
      }

      Map<String, Object> stats = new HashMap<>();
      
      // === 쿼리 실행 관련 ===
      // 총 쿼리 실행 횟수: 실제로 실행된 모든 SQL 쿼리의 총합
      stats.put("queryExecutionCount", statistics.getQueryExecutionCount());
      
      // 고유 쿼리 수: 서로 다른 쿼리 패턴의 개수
      // queryExecutionCount가 queryCount보다 훨씬 크면 N+1 문제 발생
      stats.put("queryCount", statistics.getQueries() != null ? statistics.getQueries().length : 0);
      
      // === 쿼리 성능 관련 ===
      // 가장 느린 쿼리의 실행 시간 (밀리초)
      stats.put("queryExecutionMaxTime", statistics.getQueryExecutionMaxTime());
      
      // 가장 느린 쿼리의 SQL 문 (성능 병목 지점 확인용)
      stats.put("queryExecutionMaxTimeQueryString", statistics.getQueryExecutionMaxTimeQueryString());
      
      // === 엔티티 로드 관련 ===
      // 데이터베이스에서 엔티티를 로드한 총 횟수
      stats.put("entityLoadCount", statistics.getEntityLoadCount());
      
      // === 쿼리 캐시 효율 ===
      // 쿼리 실행 계획 캐시 히트 횟수 (캐시 효율이 높을수록 좋음)
      stats.put("queryPlanCacheHitCount", statistics.getQueryPlanCacheHitCount());
      
      // 쿼리 실행 계획 캐시 미스 횟수 (새로운 쿼리 패턴이 처음 실행된 횟수)
      stats.put("queryPlanCacheMissCount", statistics.getQueryPlanCacheMissCount());

      return ResponseEntity.ok(stats);
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("error", "통계 정보를 가져오는 중 오류 발생: " + e.getMessage()));
    }
  }

  /**
   * 각 쿼리별 실행 횟수를 반환합니다.
   * N+1 문제를 진단하는 데 유용합니다.
   */
  @GetMapping("/hibernate-stats/queries")
  public ResponseEntity<Map<String, Object>> getQueryStatistics() {
    try {
      SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
      Statistics statistics = sessionFactory.getStatistics();

      if (statistics == null || !statistics.isStatisticsEnabled()) {
        return ResponseEntity.ok(Map.of("error", "Hibernate 통계가 활성화되지 않았습니다."));
      }

      // 고유 쿼리 목록 가져오기
      String[] queries = statistics.getQueries();
      
      if (queries == null || queries.length == 0) {
        return ResponseEntity.ok(Map.of(
            "message", "실행된 쿼리가 없습니다.",
            "queries", List.of()
        ));
      }

      // 각 쿼리별 통계 수집
      List<Map<String, Object>> queryStatsList = new ArrayList<>();
      
      for (String query : queries) {
        QueryStatistics queryStats = statistics.getQueryStatistics(query);
        
        if (queryStats != null) {
          long executionCount = queryStats.getExecutionCount();
          
          // 실행 횟수가 0인 쿼리는 제외 (한 번도 실행되지 않은 쿼리)
          if (executionCount == 0) {
            continue;
          }
          
          long executionMinTime = queryStats.getExecutionMinTime();
          // Long.MAX_VALUE는 "아직 측정되지 않음"을 의미하므로 null로 처리
          Long minTime = (executionMinTime == Long.MAX_VALUE) ? null : executionMinTime;
          
          Map<String, Object> queryInfo = new HashMap<>();
          queryInfo.put("query", query);
          queryInfo.put("executionCount", executionCount); // 실행 횟수
          queryInfo.put("executionMaxTime", queryStats.getExecutionMaxTime()); // 최대 실행 시간
          queryInfo.put("executionMinTime", minTime); // 최소 실행 시간 (null 가능)
          queryInfo.put("executionAvgTime", queryStats.getExecutionAvgTime()); // 평균 실행 시간
          queryInfo.put("cacheHitCount", queryStats.getCacheHitCount()); // 캐시 히트 횟수
          queryInfo.put("cacheMissCount", queryStats.getCacheMissCount()); // 캐시 미스 횟수
          
          queryStatsList.add(queryInfo);
        }
      }

      // 실행 횟수 기준으로 정렬 (많이 실행된 순서)
      queryStatsList.sort((a, b) -> {
        Long countA = (Long) a.get("executionCount");
        Long countB = (Long) b.get("executionCount");
        return countB.compareTo(countA);
      });

      Map<String, Object> result = new HashMap<>();
      result.put("totalQueries", queries.length);
      result.put("queries", queryStatsList);

      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("error", "쿼리 통계 정보를 가져오는 중 오류 발생: " + e.getMessage()));
    }
  }

  /**
   * Hibernate 통계를 리셋합니다.
   */
  @GetMapping("/hibernate-stats/reset")
  public ResponseEntity<Map<String, String>> resetHibernateStatistics() {
    hibernateStatisticsConfig.resetStatistics();
    return ResponseEntity.ok(Map.of("message", "Hibernate 통계가 리셋되었습니다."));
  }

  /**
   * Observation API를 통해 수집된 메트릭을 확인합니다.
   * @Observed 어노테이션이 적용된 메서드의 상세 통계를 확인할 수 있습니다.
   */
  @GetMapping("/observation-metrics")
  public ResponseEntity<Map<String, Object>> getObservationMetrics() {
    Map<String, Object> result = new HashMap<>();
    
    // Actuator 메트릭 엔드포인트로 리다이렉트 안내
    result.put("message", "Observation 메트릭은 Actuator 엔드포인트에서 확인할 수 있습니다.");
    result.put("endpoints", Map.of(
        "allMetrics", "/actuator/metrics",
        "userListOptimized", "/actuator/metrics/user.list.optimized",
        "userListQueries", "/actuator/metrics/user.list.optimized.queries",
        "userListEntities", "/actuator/metrics/user.list.optimized.entities",
        "userListCollections", "/actuator/metrics/user.list.optimized.collections",
        "prometheus", "/actuator/prometheus"
    ));
    result.put("note", "메서드 실행 후 메트릭이 생성됩니다. 먼저 API를 호출한 후 확인하세요.");
    
    return ResponseEntity.ok(result);
  }

  /**
   * User List API(최적화 전)의 모든 메트릭을 확인합니다.
   * 
   * 사용 방법:
   * 1. GET /profiling/hibernate-stats/reset 호출 (통계 초기화)
   * 2. GET /user/list 호출 (최적화 전)
   * 3. GET /profiling/user-list-metrics 호출하여 메트릭 확인
   */
  @GetMapping("/user-list-metrics")
  public ResponseEntity<Map<String, Object>> getUserListMetrics() {
    Map<String, Object> result = new HashMap<>();
    
    try {
      // === 최적화 전 메트릭 ===
      Map<String, Object> metrics = extractMetrics("user.list");
      result.putAll(metrics);
      
      // === Hibernate 통계 (전체) ===
      addHibernateStatistics(result);
      
      // === 사용 안내 ===
      result.put("endpoint", "GET /user/list (최적화 전)");
      
      return ResponseEntity.ok(result);
      
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
          "error", "메트릭 조회 중 오류 발생: " + e.getMessage(),
          "note", "먼저 /user/list API를 호출했는지 확인하세요."
      ));
    }
  }
  
  /**
   * User List API(최적화 후)의 모든 메트릭을 확인합니다.
   * 
   * 사용 방법:
   * 1. GET /profiling/hibernate-stats/reset 호출 (통계 초기화)
   * 2. GET /user/list/optimized 호출 (최적화 후)
   * 3. GET /profiling/user-list-optimized-metrics 호출하여 메트릭 확인
   */
  @GetMapping("/user-list-optimized-metrics")
  public ResponseEntity<Map<String, Object>> getUserListOptimizedMetrics() {
    Map<String, Object> result = new HashMap<>();
    
    try {
      // === 최적화 후 메트릭 ===
      Map<String, Object> metrics = extractMetrics("user.list.optimized");
      result.putAll(metrics);
      
      // === Hibernate 통계 (전체) ===
      addHibernateStatistics(result);
      
      // === 사용 안내 ===
      result.put("endpoint", "GET /user/list/optimized (최적화 후)");

      return ResponseEntity.ok(result);
      
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
          "error", "메트릭 조회 중 오류 발생: " + e.getMessage(),
          "note", "먼저 /user/list/optimized API를 호출했는지 확인하세요."
      ));
    }
  }
  
  /**
   * 특정 메트릭 이름에 대한 정보를 추출하는 헬퍼 메서드
   */
  private Map<String, Object> extractMetrics(String metricBaseName) {
    Map<String, Object> metrics = new HashMap<>();
    
    // 1. Timer 메트릭 (실행 시간)
    Timer timer = meterRegistry.find(metricBaseName).timer();
    if (timer != null) {
      Map<String, Object> timerStats = new HashMap<>();
      timerStats.put("count", timer.count());
      timerStats.put("totalTime", String.format("%.3f seconds", timer.totalTime(TimeUnit.MILLISECONDS) / 1000.0));
      timerStats.put("max", String.format("%.3f seconds", timer.max(TimeUnit.MILLISECONDS) / 1000.0));
      timerStats.put("mean", String.format("%.3f seconds", timer.mean(TimeUnit.MILLISECONDS) / 1000.0));
      metrics.put("executionTime", timerStats);
    } else {
      metrics.put("executionTime", "메트릭 없음 (API를 먼저 호출하세요)");
    }
    
    // 2. 쿼리 실행 횟수
    Counter queriesCounter = meterRegistry.find(metricBaseName + ".queries")
        .tag("type", "execution")
        .counter();
    if (queriesCounter != null) {
      metrics.put("queryExecutionCount", (long) queriesCounter.count());
    } else {
      metrics.put("queryExecutionCount", 0L);
    }
    
    // 3. 엔티티 로드 횟수
    Counter entitiesLoadCounter = meterRegistry.find(metricBaseName + ".entities")
        .tag("type", "load")
        .counter();
    if (entitiesLoadCounter != null) {
      metrics.put("entityLoadCount", (long) entitiesLoadCounter.count());
    } else {
      metrics.put("entityLoadCount", 0L);
    }
    
    // 4. 엔티티 페치 횟수
    Counter entitiesFetchCounter = meterRegistry.find(metricBaseName + ".entities")
        .tag("type", "fetch")
        .counter();
    if (entitiesFetchCounter != null) {
      metrics.put("entityFetchCount", (long) entitiesFetchCounter.count());
    } else {
      metrics.put("entityFetchCount", 0L);
    }
    
    // 5. 컬렉션 로드 횟수
    Counter collectionsLoadCounter = meterRegistry.find(metricBaseName + ".collections")
        .tag("type", "load")
        .counter();
    if (collectionsLoadCounter != null) {
      metrics.put("collectionLoadCount", (long) collectionsLoadCounter.count());
    } else {
      metrics.put("collectionLoadCount", 0L);
    }
    
    // 6. 트랜잭션 횟수
    Counter transactionsCounter = meterRegistry.find(metricBaseName + ".transactions").counter();
    if (transactionsCounter != null) {
      metrics.put("transactionCount", (long) transactionsCounter.count());
    } else {
      metrics.put("transactionCount", 0L);
    }
    
    return metrics;
  }
  
  /**
   * Hibernate 통계를 결과 맵에 추가하는 헬퍼 메서드
   */
  private void addHibernateStatistics(Map<String, Object> result) {
    try {
      SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
      Statistics statistics = sessionFactory.getStatistics();
      
      if (statistics != null && statistics.isStatisticsEnabled()) {
        Map<String, Object> hibernateStats = new HashMap<>();
        hibernateStats.put("totalQueryExecutionCount", statistics.getQueryExecutionCount());
        hibernateStats.put("totalEntityLoadCount", statistics.getEntityLoadCount());
        hibernateStats.put("totalEntityFetchCount", statistics.getEntityFetchCount());
        hibernateStats.put("totalCollectionLoadCount", statistics.getCollectionLoadCount());
        hibernateStats.put("uniqueQueryCount", statistics.getQueries() != null ? statistics.getQueries().length : 0);
        hibernateStats.put("queryPlanCacheHitCount", statistics.getQueryPlanCacheHitCount());
        hibernateStats.put("queryPlanCacheMissCount", statistics.getQueryPlanCacheMissCount());
        
        result.put("hibernateStatistics", hibernateStats);
      } else {
        result.put("hibernateStatistics", "Hibernate 통계가 비활성화되어 있습니다.");
      }
    } catch (Exception e) {
      result.put("hibernateStatistics", "통계 조회 실패: " + e.getMessage());
    }
  }
}

