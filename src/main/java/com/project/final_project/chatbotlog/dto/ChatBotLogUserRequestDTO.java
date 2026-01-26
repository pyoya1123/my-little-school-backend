package com.project.final_project.chatbotlog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatBotLogUserRequestDTO {
  private String user_type;
  private String message;
  private Integer userId;
}
