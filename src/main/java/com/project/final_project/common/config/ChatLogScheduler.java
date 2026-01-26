package com.project.final_project.common.config;

import com.project.final_project.airecommendation.service.AIRecommendationService;
import com.project.final_project.chatlog.domain.lastprocessedstatus.LastProcessedStatus;
import com.project.final_project.chatlog.repository.lastprocessedstatus.LastProcessedStatusRepository;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ChatLogScheduler {

  private final JobLauncher jobLauncher;
  private final Job chatLogJob;
  private final UserService userService;
  private final LastProcessedStatusRepository lastProcessedStatusRepository;

  // 유저별 lastProcessedId 관리
//  private final ConcurrentMap<Long, Long> userLastProcessedIds = new ConcurrentHashMap<>();

  @Scheduled(fixedDelay = 30000) // 30초마다 실행
  public void runChatLogJob() {
    List<UserDTO> allUser = userService.getAllUser();  // 모든 유저 조회

    for (UserDTO user : allUser) {
      Long userId = user.getId().longValue();

      // 현재 유저의 마지막 처리된 ID 가져오기 (없으면 기본값 0)
      LastProcessedStatus lps = lastProcessedStatusRepository
          .getLastProcessedStatusByUserId(userId.intValue()).orElse(null);

      if(lps == null){
        lps = new LastProcessedStatus(userId.intValue(), 0);
      }

      Long lastProcessedId = lps.getLastProcessedId().longValue();

      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("userId", userId)  // JobParameters에 유저 ID 전달
          .addLong("lastProcessedId", lastProcessedId)  // 마지막 처리된 ID 전달
          .addLong("time", System.currentTimeMillis())  // 고유한 파라미터
          .toJobParameters();

      try {
        JobExecution jobExecution = jobLauncher.run(chatLogJob, jobParameters);

        // 실행 결과에서 마지막 처리된 ID 갱신
        Long updatedLastProcessedId = (Long) jobExecution.getExecutionContext().get("lastProcessedId");
        if (updatedLastProcessedId != null) {
//          userLastProcessedIds.put(userId, updatedLastProcessedId);
            lps.setLastProcessedId(updatedLastProcessedId.intValue());
            lastProcessedStatusRepository.save(lps);
        }

      } catch (Exception e) {
        log.error("Job 실행 중 오류 발생 for userId {}: {}", userId, e.getMessage());
      }
    }
  }
}
