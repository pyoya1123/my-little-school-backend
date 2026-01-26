package com.project.final_project.chatlog.dto;

import com.project.final_project.chatlog.domain.ChatLog;
import com.project.final_project.chatlog.domain.ChatType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatLogResponseDTO {
  private Integer senderId;
  private Integer receiverId;
  private String message;
  private String timestamp;
  private String channel;
  private ChatType chatType;

  public ChatLogResponseDTO(Integer senderId, Integer receiverId, String message, String timestamp,
      String channel, ChatType chatType) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.message = message;
    this.timestamp = timestamp;
    this.channel = channel;
    this.chatType = chatType;
  }

  public ChatLogResponseDTO(ChatLog log){
    this.senderId = log.getSenderId();
    this.receiverId = log.getReceiverId();
    this.message = log.getMessage();
    this.timestamp = log.getTimestamp();
    this.channel = log.getChannel();
    this.chatType = log.getChatType();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
