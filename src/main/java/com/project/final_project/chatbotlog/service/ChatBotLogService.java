package com.project.final_project.chatbotlog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.chatbotlog.domain.ChatBotLog;
import com.project.final_project.chatbotlog.dto.ChatBotLogDTO;
import com.project.final_project.chatbotlog.dto.ChatBotLogUserRequestDTO;
import com.project.final_project.chatbotlog.repository.ChatBotLogRepository;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatBotLogService {
  private ChatBotLogRepository chatBotLogRepository;
  private RestTemplate restTemplate;

  @Autowired
  public ChatBotLogService(ChatBotLogRepository chatBotLogRepository, RestTemplate restTemplate) {
    this.chatBotLogRepository = chatBotLogRepository;
    this.restTemplate = restTemplate;
  }

  public ChatBotLogDTO requestToChatBot(ChatBotLogUserRequestDTO dto) {
    // FastAPI 서버 URL 설정
    String url = "https://certainly-legal-grubworm.ngrok-free.app/npc/";

    // 요청 본문 생성
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    // 요청 본문에 message와 user_type을 JSON 형식으로 추가
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("message", dto.getMessage() != null ? dto.getMessage() : "기본 메시지");
    requestBody.put("user_type", dto.getUser_type() != null ? dto.getUser_type() : "중학생");

    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

    try {
      // FastAPI 서버로 요청 보내기
      ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

      System.out.println(responseEntity);

      // 응답 본문에서 content 값 추출
      String aiMessage;
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

      JsonNode npcResponseNode = rootNode.path("npc_response");
      aiMessage = npcResponseNode.asText("Error: No content found");

      // ChatBotLog 객체 생성 및 저장
      ChatBotLog chatBotLog = new ChatBotLog(dto.getMessage(), aiMessage, dto.getUserId());
      ChatBotLog savedChatBotLog = chatBotLogRepository.save(chatBotLog);

      System.out.println("Saved ChatBotLog: " + savedChatBotLog);

      return new ChatBotLogDTO(savedChatBotLog);

    } catch (RestClientException e) {
      System.err.println("Error during FastAPI request: " + e.getMessage());
      return new ChatBotLogDTO(dto.getMessage(), "Error: FastAPI request failed", dto.getUserId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public List<ChatBotLogDTO> getChatBotLogListByUserId(Integer userId) {
    return chatBotLogRepository.getChatBotLogListByUserId(userId).stream()
        .map(ChatBotLogDTO::new)
        .toList();
  }

  public void deleteChatBotLogListByUserId(Integer userId) {
    chatBotLogRepository.deleteAll(chatBotLogRepository.getChatBotLogListByUserId(userId));
  }
}
