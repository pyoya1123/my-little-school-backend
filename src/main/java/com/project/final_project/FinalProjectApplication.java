package com.project.final_project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// 스케줄러 비활성화 (필요시 주석 해제)
// import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling  // 스케줄러 비활성화됨
@SpringBootApplication
public class FinalProjectApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    SpringApplication.run(FinalProjectApplication.class, args);
  }
}
