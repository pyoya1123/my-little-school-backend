package com.project.final_project.common.config;

import com.project.final_project.airecommendation.dto.AIResponseDTO;
import com.project.final_project.airecommendation.service.AIRecommendationService;
import com.project.final_project.chatlog.domain.ChatLog;
import com.project.final_project.chatlog.dto.ChatLogDTO;
import com.project.final_project.emotionanalysis.dto.EmotionAnalysisResponseDTO;
import com.project.final_project.emotionanalysis.service.EmotionAnalysisService;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class ChatLogBatchConfig {

  private final DataSource dataSource;
  private final AIRecommendationService aiRecommendationService;
  private final EmotionAnalysisService emotionAnalysisSerivce;

  @Bean
  public Job chatLogJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
    return new JobBuilder("chatLogJob", jobRepository)
        .start(chatLogStep(jobRepository, transactionManager))
        .build();
  }

  @Bean
  public Step chatLogStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
    return new StepBuilder("chatLogStep", jobRepository)
        .<ChatLog, ChatLog>chunk(10, transactionManager)
        .reader(chatLogReader(null, null))
        .writer(chatLogWriter(null))
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<ChatLog> chatLogReader(
      @Value("#{jobParameters['userId']}") Long userId,
      @Value("#{jobParameters['lastProcessedId']}") Long lastProcessedId) {

    // QueryProvider 설정
    MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
    queryProvider.setSelectClause("SELECT id, message, timestamp, sender_id");
    queryProvider.setFromClause("FROM chat_log");
    queryProvider.setWhereClause("WHERE sender_id = :senderId AND id > :lastProcessedId");
    queryProvider.setSortKeys(Collections.singletonMap("id", Order.ASCENDING));

    return new JdbcPagingItemReaderBuilder<ChatLog>()
        .dataSource(dataSource)
        .fetchSize(10)
        .rowMapper(new BeanPropertyRowMapper<>(ChatLog.class))
        .queryProvider(queryProvider)
        .parameterValues(Map.of(
            "senderId", userId != null ? userId.intValue() : 0,
            "lastProcessedId", lastProcessedId != null ? lastProcessedId.intValue() : 0))
        .name("chatLogReader")
        .build();
  }

  @Bean
  @StepScope
  public ItemWriter<ChatLog> chatLogWriter(
      @Value("#{jobParameters['userId']}") Long userId) {
    return items -> {

      // 메시지 및 타임스탬프 변환
      List<ChatLogDTO> chatLogs = items.getItems().stream().map(ChatLogDTO::new).toList();

      // AI 서버에 채팅 로그 보내기
      AIResponseDTO aiSendChatLogResponse = aiRecommendationService.sendChatLogToAI(chatLogs);
      System.out.println("aiSendChatLogResponse = " + aiSendChatLogResponse);

      // 마지막 처리한 ID 저장
      Long lastProcessedId = items.getItems().get(items.size() - 1).getId().longValue();
      StepExecution stepExecution =
          Objects.requireNonNull(StepSynchronizationManager.getContext()).getStepExecution();
      stepExecution.getJobExecution().getExecutionContext().put("lastProcessedId", lastProcessedId);
      log.info("Updated lastProcessedId: {}", lastProcessedId);

      // 추천 결과 처리
      AIResponseDTO recommendation = aiRecommendationService.getRecommendation(userId.intValue());
      System.out.println("recommendation = " + recommendation);
      aiRecommendationService.inputRecommendation(userId.intValue(), recommendation);

      // 감성 분석 처리
      EmotionAnalysisResponseDTO emotionAnalysisResponse =
          emotionAnalysisSerivce.RequestEmotionAnalysis(userId.intValue());
      System.out.println("emotionAnalysisResponse = " + emotionAnalysisResponse);

      if (recommendation != null) {
        aiRecommendationService.inputRecommendation(userId.intValue(), recommendation);
        log.info("Recommendation for userId {}: {}", userId, recommendation);
      } else {
        log.warn("No recommendation available for userId {}", userId);
      }
    };
  }


  @Bean
  public SimpleJobOperator jobOperator(
      JobLauncher jobLauncher,
      JobRepository jobRepository,
      JobRegistry jobRegistry,
      JobExplorer jobExplorer) {
    SimpleJobOperator jobOperator = new SimpleJobOperator();
    jobOperator.setJobLauncher(jobLauncher); // JobLauncher 주입
    jobOperator.setJobRepository(jobRepository);
    jobOperator.setJobRegistry(jobRegistry);
    jobOperator.setJobExplorer(jobExplorer);
    return jobOperator;
  }
}
