package com.project.final_project.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.websocket.manager.UserSessionManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Slf4j
@Component
public class KeepAliveService {
  @Autowired
  private ObjectMapper jacksonObjectMapper;

  // 스케줄러가 비활성화되어 수동 호출만 가능합니다.
  // @Scheduled(fixedRate = 50000)  // 스케줄러 비활성화됨
  public void sendKeepAlive() {
    UserSessionManager.getAllSessions().values().forEach(session -> {

      Map<String, Object> userResponse = new HashMap<>();
      userResponse.put("type", "KEEP_ALIVE");

      try {
        String userJsonResponse = jacksonObjectMapper.writeValueAsString(userResponse);

        if (session.isOpen()) {
          session.sendMessage(new TextMessage(userJsonResponse));
        }
      } catch (IOException e) {
        log.error("Failed to send keep-alive message", e);
      }
    });
  }
}
