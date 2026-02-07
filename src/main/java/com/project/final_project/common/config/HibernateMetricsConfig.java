package com.project.final_project.common.config;

import com.project.final_project.common.aspect.HibernateStatisticsObservationHandler;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hibernate Statistics를 Micrometer 메트릭으로 변환하는 설정
 * Observation API와 통합하여 메서드별 상세 통계를 수집합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class HibernateMetricsConfig {

    private final ObservationRegistry observationRegistry;
    private final HibernateStatisticsObservationHandler hibernateStatsHandler;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void registerObservationHandler() {
        // Hibernate Statistics Handler 등록
        observationRegistry.observationConfig()
            .observationHandler(hibernateStatsHandler);
    }
}


