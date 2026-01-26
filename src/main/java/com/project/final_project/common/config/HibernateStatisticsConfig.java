package com.project.final_project.common.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Hibernate 통계 정보 수집 및 로깅
 * 프로파일링을 위해 주기적으로 Hibernate 통계를 출력합니다.
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class HibernateStatisticsConfig {

  private final EntityManagerFactory entityManagerFactory;
  private Statistics statistics;

  @PostConstruct
  public void init() {
    try {
      SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
      this.statistics = sessionFactory.getStatistics();
      if (statistics != null) {
        statistics.setStatisticsEnabled(true);
        log.info("Hibernate 통계 수집이 활성화되었습니다.");
      } else {
        log.warn("Hibernate Statistics를 가져올 수 없습니다.");
      }
    } catch (Exception e) {
      log.warn("Hibernate Statistics 초기화 실패: {}", e.getMessage());
    }
  }

  /**
   * 5분마다 Hibernate 통계 정보를 로깅합니다.
   */
  @Scheduled(fixedRate = 300000) // 5분 = 300000ms
  public void logHibernateStatistics() {
    if (statistics == null || !statistics.isStatisticsEnabled()) {
      return;
    }

    log.info("=== Hibernate 통계 정보 ===");
    log.info("총 쿼리 실행 횟수: {}", statistics.getQueryExecutionCount());
    log.info("총 쿼리 실행 시간: {}ms", statistics.getQueryExecutionMaxTime());
    log.info("평균 쿼리 실행 시간: {}ms", 
        statistics.getQueryExecutionCount() > 0 
            ? statistics.getQueryExecutionMaxTime() / statistics.getQueryExecutionCount() 
            : 0);
    log.info("총 엔티티 로드 횟수: {}", statistics.getEntityLoadCount());
    log.info("총 엔티티 페치 횟수: {}", statistics.getEntityFetchCount());
    log.info("총 컬렉션 로드 횟수: {}", statistics.getCollectionLoadCount());
    log.info("총 컬렉션 페치 횟수: {}", statistics.getCollectionFetchCount());
    log.info("총 세션 열기 횟수: {}", statistics.getSessionOpenCount());
    log.info("총 세션 닫기 횟수: {}", statistics.getSessionCloseCount());
    log.info("총 트랜잭션 횟수: {}", statistics.getTransactionCount());
    log.info("총 성공한 트랜잭션 횟수: {}", statistics.getSuccessfulTransactionCount());
    log.info("연결 횟수: {}", statistics.getConnectCount());
    log.info("준비된 문장 캐시 히트: {}", statistics.getQueryPlanCacheHitCount());
    log.info("준비된 문장 캐시 미스: {}", statistics.getQueryPlanCacheMissCount());
    
    // 실행된 쿼리 통계
    String[] queries = statistics.getQueries();
    if (queries != null && queries.length > 0) {
      log.info("실행된 고유 쿼리 수: {}", queries.length);
    }
    
    log.info("========================");
  }

  /**
   * 통계 정보를 리셋합니다.
   */
  public void resetStatistics() {
    if (statistics != null) {
      statistics.clear();
      log.info("Hibernate 통계 정보가 리셋되었습니다.");
    }
  }
}

