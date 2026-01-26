package com.project.final_project.chatlog.service;

import com.project.final_project.chatlog.domain.ChatLog;
import com.project.final_project.chatlog.dto.ChatLogRequestDTO;
import com.project.final_project.chatlog.dto.ChatLogResponseDTO;
import com.project.final_project.chatlog.repository.ChatLogRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatLogService {

  private final ChatLogRepository chatLogRepository;

  public ChatLogResponseDTO saveChatLog(ChatLogRequestDTO chatLogRequestDTO) {
    ChatLog chatLog = ChatLog.builder()
        .senderId(chatLogRequestDTO.getSenderId())
        .receiverId(chatLogRequestDTO.getReceiverId())
        .message(chatLogRequestDTO.getMessage())
        .channel(chatLogRequestDTO.getChannel())
        .timestamp(chatLogRequestDTO.getTimestamp())
        .chatType(chatLogRequestDTO.getChatType())
        .build();
    chatLogRepository.save(chatLog);
    return new ChatLogResponseDTO(chatLog);
  }

  public List<ChatLogResponseDTO> getChatLogsBySenderId(Integer senderId) {
    List<ChatLog> list = chatLogRepository.getChatLogsBySenderId(senderId);
    return list.stream()
        .map(c -> ChatLogResponseDTO.builder().
            senderId(c.getSenderId())
            .receiverId(c.getReceiverId())
            .message(c.getMessage())
            .channel(c.getChannel())
            .timestamp(c.getTimestamp())
            .chatType(c.getChatType())
            .build()
        )
        .collect(Collectors.toList());
  }

  // 채팅 로그가 이미 있는지 확인하는 메서드
  public boolean existsChatLogs() {
    return chatLogRepository.count() > 0;  // 데이터가 있으면 true 반환
  }

  public List<ChatLogResponseDTO> getAllChatLogs() {
    List<ChatLog> allChatLogs = chatLogRepository.findAll();
    return allChatLogs.stream()
        .map(c -> new ChatLogResponseDTO(
            c.getSenderId(),
            c.getReceiverId(),
            c.getMessage(),
            c.getTimestamp(),
            c.getChannel(),
            c.getChatType())
        )
        .toList();
  }

  @Transactional
  public void deleteChatLogListByUserId(Integer userId) {
    chatLogRepository.deleteAll(chatLogRepository.getChatLogsBySenderId(userId));
  }
}
