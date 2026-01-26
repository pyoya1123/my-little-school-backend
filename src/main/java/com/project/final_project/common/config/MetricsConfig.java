package com.project.final_project.common.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer 메트릭 설정
 * 프로파일링을 위한 커스텀 메트릭을 정의합니다.
 */
@Configuration
public class MetricsConfig {

  /**
   * @Timed 어노테이션을 사용하여 메서드 실행 시간을 측정할 수 있도록 합니다.
   */
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  /**
   * 데이터베이스 쿼리 성능 측정을 위한 Timer 생성
   */
  @Bean
  public Timer databaseQueryTimer(MeterRegistry registry) {
    return Timer.builder("database.query.duration")
        .description("데이터베이스 쿼리 실행 시간")
        .register(registry);
  }

  /**
   * API 엔드포인트 응답 시간 측정을 위한 Timer 생성
   */
  @Bean
  public Timer apiResponseTimer(MeterRegistry registry) {
    return Timer.builder("api.response.duration")
        .description("API 엔드포인트 응답 시간")
        .register(registry);
  }
}

