package com.project.final_project.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Hibernate 세션 메트릭 로깅 비활성화 설정
 * 
 * application.yml에서 로깅 레벨을 OFF로 설정하여
 * StatisticalLoggingSessionEventListener의 로그를 비활성화합니다.
 * 
 * 로깅 레벨 설정:
 * org.hibernate.event.internal.StatisticalLoggingSessionEventListener: OFF
 */
@Slf4j
@Configuration
public class HibernateSessionMetricsConfig {
    
    // application.yml의 로깅 레벨 설정으로 충분하므로
    // 별도의 프로그래매틱 설정은 불필요합니다.
    // 필요시 여기에 추가 설정을 구현할 수 있습니다.
}

