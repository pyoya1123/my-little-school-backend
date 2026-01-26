package com.project.final_project.common.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 서비스 레이어 성능 측정 AOP
 * @Timed 어노테이션이 없는 메서드도 측정할 수 있도록 합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceAspect {

  private final MeterRegistry meterRegistry;

  /**
   * Service 레이어의 모든 메서드 실행 시간을 측정합니다.
   */
  @Around("execution(* com.project.final_project..service..*(..))")
  public Object measureServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    String metricName = "service.method.duration";

    Timer.Sample sample = Timer.start(meterRegistry);
    
    try {
      Object result = joinPoint.proceed();
      return result;
    } finally {
      sample.stop(Timer.builder(metricName)
          .description("서비스 메서드 실행 시간")
          .tag("class", className)
          .tag("method", methodName)
          .register(meterRegistry));
    }
  }

  /**
   * Repository 레이어의 모든 메서드 실행 시간을 측정합니다.
   */
  @Around("execution(* com.project.final_project..repository..*(..))")
  public Object measureRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    String metricName = "repository.method.duration";

    Timer.Sample sample = Timer.start(meterRegistry);
    
    try {
      Object result = joinPoint.proceed();
      return result;
    } finally {
      sample.stop(Timer.builder(metricName)
          .description("Repository 메서드 실행 시간")
          .tag("class", className)
          .tag("method", methodName)
          .register(meterRegistry));
    }
  }
}

