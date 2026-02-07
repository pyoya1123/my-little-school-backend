package com.project.final_project.common.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer Observation API 설정
 * Spring Boot 3.x의 최신 메트릭 측정 방식
 * Metrics와 Tracing을 통합 관리합니다.
 * 
 * Hibernate Statistics와 통합하여 쿼리 실행 횟수, 엔티티 로드 횟수 등을 측정합니다.
 */
@Configuration
public class ObservationConfig {

  /**
   * @Observed 어노테이션을 사용하기 위한 Aspect 빈 등록
   * ObservationRegistry는 Spring Boot가 자동으로 제공합니다.
   * 
   * HibernateStatisticsObservationHandler가 자동으로 등록되어
   * 쿼리 통계를 수집합니다.
   */
  @Bean
  public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }
}

