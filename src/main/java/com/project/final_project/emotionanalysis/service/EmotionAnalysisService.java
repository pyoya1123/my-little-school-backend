package com.project.final_project.emotionanalysis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.project.final_project.emotionanalysis.domain.EmotionAnalysis;
import com.project.final_project.emotionanalysis.dto.EmotionAnalysisResponseDTO;
import com.project.final_project.emotionanalysis.repository.EmotionRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

  private final EmotionRepository emotionRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String AI_EMOTION_ANALYSIS_URL =
      "https://certainly-legal-grubworm.ngrok-free.app/emotions/";

  @Transactional
  public EmotionAnalysisResponseDTO RequestEmotionAnalysis(Integer userId) {

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("senderId", userId);

    System.out.println("request emotion analysis requestBody = " + requestBody);

    String responseBody;
    try {
      // JSON 문자열로 변환
      String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
      System.out.println("requestBody emotion (JSON) = " + jsonRequestBody);

      // HTTP 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // HTTP 요청 엔티티 생성
      HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

      // REST API 요청
      responseBody = restTemplate.postForObject(AI_EMOTION_ANALYSIS_URL, entity, String.class);
      System.out.println("responseBody = " + responseBody);

    } catch (HttpServerErrorException e) {
      log.error("AI 서버 오류 발생: {}", e.getResponseBodyAsString());
      throw new RuntimeException("AI 서버 오류 발생", e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    EmotionAnalysisResponseDTO emotionResponse = null;
    try {
      JsonNode rootNode = objectMapper.readTree(responseBody);

      // "emotions" 키에서 ArrayNode를 추출
      if (!rootNode.has("emotions") || !rootNode.get("emotions").isArray()) {
        log.error("AI 응답 데이터에 'emotions' 필드가 없습니다 또는 배열 형태가 아닙니다.");
        throw new RuntimeException("AI 응답 데이터에 'emotions' 필드가 없습니다 또는 배열 형태가 아닙니다.");
      }

      ArrayNode emotionsArray = (ArrayNode) rootNode.get("emotions");

      if (emotionsArray.isEmpty()) {
        log.error("AI 응답 데이터의 'emotions' 배열이 비어 있습니다.");
        throw new RuntimeException("AI 응답 데이터의 'emotions' 배열이 비어 있습니다.");
      }

      // 첫 번째 감정 분석 결과만 처리
      JsonNode firstEmotion = emotionsArray.get(0);

      if (!firstEmotion.has("message")
          || !firstEmotion.has("emotion")
          || !firstEmotion.has("score")) {
        log.error("AI 응답 데이터에 필요한 필드가 없습니다.");
        throw new RuntimeException("AI 응답 데이터에 필요한 필드가 없습니다.");
      }

      String message = firstEmotion.get("message").asText();
      String emotion = firstEmotion.get("emotion").asText();
      Double score = firstEmotion.get("score").asDouble();

      EmotionAnalysis foundEmotionAnalysis = emotionRepository.getEmotionAnalysisByUserId(userId);

      if (foundEmotionAnalysis != null) {
        foundEmotionAnalysis.setMessage(message);
        foundEmotionAnalysis.setEmotion(emotion);
        foundEmotionAnalysis.setScore(score);
      } else {
        emotionResponse = new EmotionAnalysisResponseDTO(message, emotion, score);
        emotionRepository.save(
            new EmotionAnalysis(message, emotion, score, userId));
      }

    } catch (JsonMappingException e) {
      log.error("JSON 매핑 오류: {}", e.getMessage());
      throw new RuntimeException("JSON 매핑 오류", e);
    } catch (JsonProcessingException e) {
      log.error("JSON 처리 오류: {}", e.getMessage());
      throw new RuntimeException("JSON 처리 오류", e);
    }

    return emotionResponse;
  }

  public EmotionAnalysisResponseDTO getEmotionAnalysisByUserId(Integer userId) {
    return new EmotionAnalysisResponseDTO(emotionRepository.getEmotionAnalysisByUserId(userId));
  }

  @Transactional
  public void deleteUserLogsByUserId(Integer userId) {
    emotionRepository.delete(emotionRepository.getEmotionAnalysisByUserId(userId));
  }
}