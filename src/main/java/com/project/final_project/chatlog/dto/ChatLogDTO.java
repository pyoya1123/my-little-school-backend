package com.project.final_project.chatlog.dto;

import com.project.final_project.chatlog.domain.ChatLog;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogDTO {

  Integer userId;
  String message;
  String timestamp;

  public ChatLogDTO(ChatLog chatLog) {
    this.userId = chatLog.getSenderId();
    this.message = chatLog.getMessage();
    this.timestamp = chatLog.getTimestamp();
  }

  @Override
  public String toString() {
    return "ChatLogDTO{" +
        "userId=" + userId +
        ", message='" + message + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}
