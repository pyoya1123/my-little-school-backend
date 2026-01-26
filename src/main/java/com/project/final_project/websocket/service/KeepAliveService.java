package com.project.final_project.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.websocket.manager.UserSessionManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class KeepAliveService {
  @Autowired
  private ObjectMapper jacksonObjectMapper;

  @Scheduled(fixedRate = 50000) // 50초마다 실행
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
        e.printStackTrace();
      }
    });
  }
}
