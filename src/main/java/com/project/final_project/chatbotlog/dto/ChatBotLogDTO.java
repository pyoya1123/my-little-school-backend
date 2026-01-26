package com.project.final_project.chatbotlog.dto;

import com.project.final_project.chatbotlog.domain.ChatBotLog;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatBotLogDTO {
  private String userMessage;
  private String aiMessage;
  private Integer userId;

  public ChatBotLogDTO(ChatBotLog log) {
    this.userMessage = log.getUserMessage();
    this.aiMessage = log.getAiMessage();
    this.userId = log.getUserId();
  }
}
