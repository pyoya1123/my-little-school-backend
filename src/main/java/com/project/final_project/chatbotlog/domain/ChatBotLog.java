package com.project.final_project.chatbotlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat-bot-log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatBotLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "chat_bot_log_user_message")
  private String userMessage;

  @Column(name = "chat_bot_log_ai_message")
  private String aiMessage;

  @Column(name = "user_id")
  private Integer userId;

  public ChatBotLog(String userMessage, String aiMessage) {
    this.userMessage = userMessage;
    this.aiMessage = aiMessage;
  }

  public ChatBotLog(String uesrMessage, String aiMessage, Integer userId) {
    this.userMessage = uesrMessage;
    this.aiMessage = aiMessage;
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "ChatBotLog{" +
        "id=" + id +
        ", userMessage='" + userMessage + '\'' +
        ", aiMessage='" + aiMessage + '\'' +
        ", userId=" + userId +
        '}';
  }
}
