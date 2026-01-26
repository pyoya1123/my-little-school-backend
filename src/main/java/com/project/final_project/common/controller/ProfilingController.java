package com.project.final_project.common.controller;

import com.project.final_project.common.config.HibernateStatisticsConfig;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
      stats.put("queryExecutionCount", statistics.getQueryExecutionCount());
      stats.put("queryExecutionMaxTime", statistics.getQueryExecutionMaxTime());
      stats.put("queryExecutionMaxTimeQueryString", statistics.getQueryExecutionMaxTimeQueryString());
      stats.put("averageQueryExecutionTime", 
          statistics.getQueryExecutionCount() > 0 
              ? statistics.getQueryExecutionMaxTime() / statistics.getQueryExecutionCount() 
              : 0);
      stats.put("entityLoadCount", statistics.getEntityLoadCount());
      stats.put("entityFetchCount", statistics.getEntityFetchCount());
      stats.put("collectionLoadCount", statistics.getCollectionLoadCount());
      stats.put("collectionFetchCount", statistics.getCollectionFetchCount());
      stats.put("sessionOpenCount", statistics.getSessionOpenCount());
      stats.put("sessionCloseCount", statistics.getSessionCloseCount());
      stats.put("transactionCount", statistics.getTransactionCount());
      stats.put("successfulTransactionCount", statistics.getSuccessfulTransactionCount());
      stats.put("connectCount", statistics.getConnectCount());
      stats.put("queryPlanCacheHitCount", statistics.getQueryPlanCacheHitCount());
      stats.put("queryPlanCacheMissCount", statistics.getQueryPlanCacheMissCount());
      stats.put("queryCount", statistics.getQueries() != null ? statistics.getQueries().length : 0);

      return ResponseEntity.ok(stats);
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("error", "통계 정보를 가져오는 중 오류 발생: " + e.getMessage()));
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
}

