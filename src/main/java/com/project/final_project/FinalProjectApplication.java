package com.project.final_project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FinalProjectApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    SpringApplication.run(FinalProjectApplication.class, args);
  }
}
