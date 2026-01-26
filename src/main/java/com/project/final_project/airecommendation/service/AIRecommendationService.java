package com.project.final_project.airecommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.airecommendation.domain.AIRecommendation;
import com.project.final_project.airecommendation.dto.AIResponseDTO;
import com.project.final_project.airecommendation.dto.AIResponseDTO.RecommendedUser;
import com.project.final_project.airecommendation.dto.RecommendResponseDTO;
import com.project.final_project.airecommendation.dto.UserRecomendByInterestRequestDTO;
import com.project.final_project.airecommendation.repository.AIRecommendationRepository;
import com.project.final_project.chatlog.domain.ChatLog;
import com.project.final_project.chatlog.dto.ChatLogDTO;
import com.project.final_project.friendship.service.FriendshipService;
import com.project.final_project.school.domain.School;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.repository.UserRepository;
import com.project.final_project.user.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.batch.item.Chunk;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
@RequiredArgsConstructor
public class AIRecommendationService {

  private final AIRecommendationRepository aiRecommendationRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final UserRepository userRepository;

  private static final String AI_CHAT_URL = "https://certainly-legal-grubworm.ngrok-free.app/chat_logs/";
  private static final String AI_INTEREST_URL = "https://certainly-legal-grubworm.ngrok-free.app/save_interests/";
  private static final String AI_RECOMMNED_URL = "https://certainly-legal-grubworm.ngrok-free.app/recommend/";
  private final FriendshipService friendshipService;

  // 20개의 ChatLog 메시지를 객체 형태로 추출하여 AI 서버에 전송
  public AIResponseDTO sendChatLogToAI(List<ChatLogDTO> chatLogs) {

    // AI 서버에 보낼 요청 바디 생성
    if (chatLogs.isEmpty()) {
      throw new IllegalArgumentException("chatLogs 리스트가 비어 있습니다.");
    }

    Map<String, Object> requestBody = new HashMap<>();

    // senderId 추출 (ChatLogDTO의 첫 번째 객체의 userId 사용)
    int senderId = chatLogs.get(0).getUserId(); // ChatLogDTO에서 userId를 가져오는 메서드
    requestBody.put("senderId", senderId);

    // messages 배열 생성
    List<Map<String, String>> messages = chatLogs.stream()
        .map(chatLog -> {
          Map<String, String> messageMap = new LinkedHashMap<>();
          messageMap.put("message", chatLog.getMessage());
          messageMap.put("timestamp", chatLog.getTimestamp());
          return messageMap;
        })
        .collect(Collectors.toList());
    requestBody.put("messages", messages);

    try {
      // JSON 문자열로 변환
      String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
      System.out.println("requestBody (JSON) = " + jsonRequestBody);

      // HTTP 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // HTTP 요청 엔티티 생성
      HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

      // REST API 요청
      String responseBody = restTemplate.postForObject(AI_CHAT_URL, entity, String.class);
      System.out.println("responseBody = " + responseBody);

      // JSON 응답 처리
      AIResponseDTO aiResponse = null;
      JsonNode rootNode = objectMapper.readTree(responseBody);

      // recommended_users 필드가 없는 경우 확인
      if (!rootNode.has("recommended_users") && rootNode.get("message") != null) {
        log.info("첫 채팅 로그 임베딩 완료 {}", responseBody);
        return null;
      }

      // recommended_users 필드가 있을 경우 처리
      aiResponse = objectMapper.treeToValue(rootNode, AIResponseDTO.class);
      return aiResponse;

    } catch (JsonProcessingException e) {
      throw new RuntimeException("응답 JSON 파싱 중 오류 발생", e);
    } catch (HttpServerErrorException e) {
      log.error("AI 서버 오류 발생: {}", e.getResponseBodyAsString());
      throw new RuntimeException("AI 서버 오류 발생", e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("예상치 못한 오류 발생", e);
    }
  }


  public void sendInterestToAI(UserRecomendByInterestRequestDTO dto) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("senderId", dto.getUserId());
    requestBody.put("interests", dto.getInterest());

    // RestTemplate를 사용하여 동기식 요청 수행
    String responseBody;
    try {
      responseBody = restTemplate.postForObject(AI_INTEREST_URL, requestBody, String.class);
      System.out.println("responseBody = " + responseBody);
      // 응답 본문 처리 코드
    } catch (HttpServerErrorException e) {
      log.error("AI 서버 오류 발생: {}", e.getResponseBodyAsString());
      throw new RuntimeException("AI 서버 오류 발생", e);
    }
  }

  public AIResponseDTO getRecommendation(Integer userId) {
    String url = UriComponentsBuilder
        .fromHttpUrl(AI_RECOMMNED_URL)
        .queryParam("senderId", userId)
        .toUriString();

    try {
      ResponseEntity<AIResponseDTO> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          null,
          AIResponseDTO.class
      );

      AIResponseDTO aiResponse = response.getBody();
      System.out.println("aiResponse = " + aiResponse);

      return aiResponse;
    } catch (Exception e) {
      System.err.println("추천 데이터를 가져오는 중 오류 발생: " + e.getMessage());
      return null; // 실패 시 null 반환 또는 에러 처리 로직
    }
  }

  @Transactional
  public void inputRecommendation(Integer userId, AIResponseDTO recommendation) {

    for (RecommendedUser recommendedUser : recommendation.getRecommended_users()) {

      if(friendshipService.isFriend(userId, recommendedUser.getSenderId())){
        System.out.println(userId + ", " + recommendedUser.getSenderId());
        continue;
      }

      AIRecommendation aiRecommendation = aiRecommendationRepository
          .getRecommendationByUserIdAndRecommendedUserId(userId, recommendedUser.getSenderId())
          .orElse(null);

      if(aiRecommendation == null) {
        aiRecommendation = new AIRecommendation(
            userId,
            recommendedUser.getSenderId(),
            recommendedUser.getSimilarity(),
            recommendedUser.getMessage(),
            recommendedUser.getInterests());
      }
      else {
        if(recommendedUser.getSenderId() != null) {
          aiRecommendation.setRecommendedUserId(recommendedUser.getSenderId());
        }

        if(recommendedUser.getSimilarity() != null) {
          aiRecommendation.setSimilarity(recommendedUser.getSimilarity());
        }

        if(recommendedUser.getMessage() != null) {
          aiRecommendation.setSimilarityMessage(recommendedUser.getMessage());
        }

        if(recommendedUser.getInterests() != null && !recommendedUser.getInterests().isEmpty()) {
          aiRecommendation.setRecommendedInterestList(recommendedUser.getInterests());
        }
      }

      aiRecommendationRepository.save(aiRecommendation);
    }

  }

  public List<RecommendResponseDTO> getRecommendationInfoList(Integer userId) {
    List<RecommendResponseDTO> res = new ArrayList<>();

    // AI 추천 데이터를 가져옴
    List<AIRecommendation> all = aiRecommendationRepository.findAIRecommendationListByUserId(userId);

    all.forEach(ar -> {
      // 추천된 사용자를 조회
      User recommendedUser = userRepository.findById(ar.getRecommendedUserId()).orElseThrow(
          () -> new IllegalStateException("not found user id: " + ar.getRecommendedUserId()));

      // 친구 관계 확인
      Boolean isFriend = friendshipService.isFriend(userId, recommendedUser.getId());

      // 이미 친구라면 추천 리스트에서 제외
      if (!isFriend) {
        RecommendResponseDTO rrDTO = getRecommendResponseDTO(ar, recommendedUser);
        res.add(rrDTO);
      }
    });

    return res;
  }

  private static RecommendResponseDTO getRecommendResponseDTO(AIRecommendation ar, User recommendedUser) {
    School recommendedUserSchool = recommendedUser.getSchool();

    RecommendResponseDTO rrDTO = new RecommendResponseDTO(
        recommendedUser.getId(),
        recommendedUser.getName(),
        ar.getSimilarity(),
        recommendedUser.getIsOnline(),
        "매우 추천",
        recommendedUser.getGrade(),
        recommendedUserSchool == null ? "not registered school" : recommendedUserSchool.getLocation(),
        recommendedUser.getInterest(),
        ar.getSimilarityMessage(),
        "user avartar image url"
    );
    return rrDTO;
  }

  public void deleteRecommendationInfo(Integer recommendationInfoId) {
    aiRecommendationRepository.deleteById(recommendationInfoId);
  }

  @Transactional
  public void deleteRecommendationListByUserId(Integer userId) {
    aiRecommendationRepository.deleteAll(
        aiRecommendationRepository.getRecommendationByUserId(userId));
    aiRecommendationRepository.deleteAll(
        aiRecommendationRepository.getRecommendationByRecommendedUserId(userId));
  }
}
