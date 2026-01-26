package com.project.final_project.common.config;

import com.project.final_project.common.interceptor.PerformanceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 * 성능 측정 인터셉터를 등록합니다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final PerformanceInterceptor performanceInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(performanceInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/actuator/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-ui-custom.html",
            "/error"
        );
  }
}

