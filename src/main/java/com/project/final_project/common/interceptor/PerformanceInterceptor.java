package com.project.final_project.common.interceptor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 성능 측정 인터셉터
 * 각 HTTP 요청의 응답 시간을 측정하고 메트릭으로 수집합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceInterceptor implements HandlerInterceptor {

  private final MeterRegistry meterRegistry;
  private static final String TIMER_NAME = "http.request.duration";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 요청 시작 시간을 request attribute에 저장
    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex) {
    
    Long startTime = (Long) request.getAttribute("startTime");
    if (startTime != null) {
      long duration = System.currentTimeMillis() - startTime;
      
      // 메트릭 수집
      Timer.Sample sample = Timer.start(meterRegistry);
      sample.stop(Timer.builder(TIMER_NAME)
          .description("HTTP 요청 처리 시간")
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
}

