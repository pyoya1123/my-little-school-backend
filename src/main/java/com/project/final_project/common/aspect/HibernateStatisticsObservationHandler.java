package com.project.final_project.common.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

/**
 * Observation API와 Hibernate Statistics를 결합한 커스텀 Handler
 * 메서드 실행 시 쿼리 실행 횟수, 엔티티 로드 횟수 등을 측정합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HibernateStatisticsObservationHandler implements ObservationHandler<Observation.Context> {

    private final EntityManagerFactory entityManagerFactory;
    private final MeterRegistry meterRegistry;
    private Statistics statistics;

    @Override
    public void onStart(Observation.Context context) {
        // Observation 시작 시 Hibernate Statistics 스냅샷 저장
        if (statistics == null) {
            try {
                SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
                this.statistics = sessionFactory.getStatistics();
            } catch (Exception e) {
                log.warn("Hibernate Statistics를 가져올 수 없습니다: {}", e.getMessage());
                return;
            }
        }

        if (statistics != null && statistics.isStatisticsEnabled()) {
            // 시작 시점의 통계를 Context에 저장
            StatisticsSnapshot startSnapshot = new StatisticsSnapshot(statistics);
            context.put("hibernate.stats.start", startSnapshot);
            log.debug("[Observation] {} 시작 - 쿼리: {}", context.getName(), startSnapshot.queryExecutionCount);
        } else {
            log.warn("[Observation] Hibernate Statistics가 비활성화되어 있습니다.");
        }
    }

    @Override
    public void onStop(Observation.Context context) {
        if (statistics == null || !statistics.isStatisticsEnabled()) {
            log.warn("[Observation] {} 종료 - Hibernate Statistics 비활성화", context.getName());
            return;
        }

        StatisticsSnapshot startSnapshot = context.get("hibernate.stats.start");
        if (startSnapshot == null) {
            log.warn("[Observation] {} 종료 - 시작 스냅샷을 찾을 수 없습니다.", context.getName());
            return;
        }

        // 종료 시점의 통계
        StatisticsSnapshot endSnapshot = new StatisticsSnapshot(statistics);

        // 차이 계산
        StatisticsDiff diff = startSnapshot.diff(endSnapshot);

        // Observation 이름 가져오기
        String observationName = context.getName();
        if (observationName == null || observationName.isEmpty()) {
            observationName = "unknown";
        }

        // Context에 통계 정보 저장 (나중에 메트릭으로 등록 가능)
        context.put("hibernate.stats.diff", diff);
        context.put("hibernate.stats.queryCount", diff.getQueryExecutionCount());
        context.put("hibernate.stats.entityLoadCount", diff.getEntityLoadCount());
        context.put("hibernate.stats.collectionLoadCount", diff.getCollectionLoadCount());

        // 로그 출력
        log.info("[Observation] {} 종료 - 쿼리: {}, 엔티티 로드: {}, 컬렉션 로드: {}",
            observationName,
            diff.getQueryExecutionCount(),
            diff.getEntityLoadCount(),
            diff.getCollectionLoadCount());
        
        // 메트릭 직접 등록 (순서 문제 해결)
        registerMetrics(observationName, diff);
    }
    
    /**
     * Hibernate 통계를 Micrometer 메트릭으로 등록
     */
    private void registerMetrics(String observationName, StatisticsDiff diff) {
        try {
            // 쿼리 실행 횟수
            if (diff.getQueryExecutionCount() > 0) {
                meterRegistry.counter(observationName + ".queries", 
                    "type", "execution")
                    .increment(diff.getQueryExecutionCount());
                log.info("[Metrics] Counter 증가: {}.queries = {}", 
                    observationName, diff.getQueryExecutionCount());
            }

            // 엔티티 로드 횟수
            if (diff.getEntityLoadCount() > 0) {
                meterRegistry.counter(observationName + ".entities",
                    "type", "load")
                    .increment(diff.getEntityLoadCount());
            }

            // 엔티티 페치 횟수
            if (diff.getEntityFetchCount() > 0) {
                meterRegistry.counter(observationName + ".entities",
                    "type", "fetch")
                    .increment(diff.getEntityFetchCount());
            }

            // 컬렉션 로드 횟수
            if (diff.getCollectionLoadCount() > 0) {
                meterRegistry.counter(observationName + ".collections",
                    "type", "load")
                    .increment(diff.getCollectionLoadCount());
            }

            // 컬렉션 페치 횟수
            if (diff.getCollectionFetchCount() > 0) {
                meterRegistry.counter(observationName + ".collections",
                    "type", "fetch")
                    .increment(diff.getCollectionFetchCount());
            }

            // 세션 열기 횟수
            if (diff.getSessionOpenCount() > 0) {
                meterRegistry.counter(observationName + ".sessions",
                    "type", "open")
                    .increment(diff.getSessionOpenCount());
            }

            // 트랜잭션 횟수
            if (diff.getTransactionCount() > 0) {
                meterRegistry.counter(observationName + ".transactions")
                    .increment(diff.getTransactionCount());
            }
        } catch (Exception e) {
            log.error("[Metrics] 메트릭 등록 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    /**
     * Hibernate Statistics 스냅샷
     */
    private static class StatisticsSnapshot {
        private final long queryExecutionCount;
        private final long entityLoadCount;
        private final long entityFetchCount;
        private final long collectionLoadCount;
        private final long collectionFetchCount;
        private final long sessionOpenCount;
        private final long transactionCount;
        private final long connectCount;

        public StatisticsSnapshot(Statistics statistics) {
            this.queryExecutionCount = statistics.getQueryExecutionCount();
            this.entityLoadCount = statistics.getEntityLoadCount();
            this.entityFetchCount = statistics.getEntityFetchCount();
            this.collectionLoadCount = statistics.getCollectionLoadCount();
            this.collectionFetchCount = statistics.getCollectionFetchCount();
            this.sessionOpenCount = statistics.getSessionOpenCount();
            this.transactionCount = statistics.getTransactionCount();
            this.connectCount = statistics.getConnectCount();
        }

        public StatisticsDiff diff(StatisticsSnapshot other) {
            return new StatisticsDiff(
                other.queryExecutionCount - this.queryExecutionCount,
                other.entityLoadCount - this.entityLoadCount,
                other.entityFetchCount - this.entityFetchCount,
                other.collectionLoadCount - this.collectionLoadCount,
                other.collectionFetchCount - this.collectionFetchCount,
                other.sessionOpenCount - this.sessionOpenCount,
                other.transactionCount - this.transactionCount,
                other.connectCount - this.connectCount
            );
        }
    }

    /**
     * 통계 차이값
     */
    public static class StatisticsDiff {
        private final long queryExecutionCount;
        private final long entityLoadCount;
        private final long entityFetchCount;
        private final long collectionLoadCount;
        private final long collectionFetchCount;
        private final long sessionOpenCount;
        private final long transactionCount;
        private final long connectCount;

        public StatisticsDiff(
            long queryExecutionCount,
            long entityLoadCount,
            long entityFetchCount,
            long collectionLoadCount,
            long collectionFetchCount,
            long sessionOpenCount,
            long transactionCount,
            long connectCount
        ) {
            this.queryExecutionCount = queryExecutionCount;
            this.entityLoadCount = entityLoadCount;
            this.entityFetchCount = entityFetchCount;
            this.collectionLoadCount = collectionLoadCount;
            this.collectionFetchCount = collectionFetchCount;
            this.sessionOpenCount = sessionOpenCount;
            this.transactionCount = transactionCount;
            this.connectCount = connectCount;
        }

        public long getQueryExecutionCount() {
            return queryExecutionCount;
        }

        public long getEntityLoadCount() {
            return entityLoadCount;
        }

        public long getEntityFetchCount() {
            return entityFetchCount;
        }

        public long getCollectionLoadCount() {
            return collectionLoadCount;
        }

        public long getCollectionFetchCount() {
            return collectionFetchCount;
        }

        public long getSessionOpenCount() {
            return sessionOpenCount;
        }

        public long getTransactionCount() {
            return transactionCount;
        }

        public long getConnectCount() {
            return connectCount;
        }
    }
}

