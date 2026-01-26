package com.project.final_project.chatlog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.final_project.chatlog.domain.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogRequestDTO {

  private Integer senderId;
  private Integer receiverId;
  private String message;
  private String timestamp;
  private String channel;
  private ChatType chatType;
}
