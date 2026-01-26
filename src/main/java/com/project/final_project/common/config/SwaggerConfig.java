package com.project.final_project.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class SwaggerConfig {

  // http://localhost:8080/swagger-ui.html

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("springdoc-openapi")
            .version("1.0")
            .description("springdoc-openapi swagger-ui 화면입니다.")
        );
  }
}
