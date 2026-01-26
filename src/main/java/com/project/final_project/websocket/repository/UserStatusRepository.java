package com.project.final_project.websocket.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserStatusRepository {

  private final Map<Integer, String> userStatuses = new ConcurrentHashMap<>();

  @Autowired
  private ObjectMapper jacksonObjectMapper;

  // 유저 상태 저장
  public void setUserStatus(Integer userId, String status) {
    System.out.println("status = " + status);
    userStatuses.put(userId, status);
  }
  /*
  * userId가 userStatuses 맵에 이미 존재하면, 해당 키의 값을 새 status로 덮어씁니다.
    userId가 존재하지 않으면 새로운 키/값 쌍을 추가합니다.
  */

  // 유저 상태 삭제
  public void removeUser(Integer userId) {
    userStatuses.remove(userId);
  }

  // 유저 상태 조회
  public Map<Integer, String> getAllStatuses() {
    return userStatuses;
  }

  public String getStatus(Integer userId) {

    Map<String, Object> defaultUserResponse = new HashMap<>();
    defaultUserResponse.put("type", "OFFLINE_USER");
    defaultUserResponse.put("userId", userId);

    String defaultUserJsonResponse = null;

    try {
      defaultUserJsonResponse = jacksonObjectMapper.writeValueAsString(defaultUserResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return userStatuses.getOrDefault(userId, defaultUserJsonResponse);
  }


  public List<Map<String, Object>> getUserStatusByMapIdAndMapType(Integer mapId, String mapType) {
    System.out.println("userStatuses = " + userStatuses);

    List<Map<String, Object>> matchingUsers = new ArrayList<>();

    for (Map.Entry<Integer, String> entry : userStatuses.entrySet()) {
      try {
        // JSON 문자열을 Map으로 변환
        Map<String, Object> userStatus = jacksonObjectMapper.readValue(entry.getValue(), Map.class);

        // mapId와 mapType이 일치하는지 확인
        if (userStatus.get("mapId").equals(mapId) && userStatus.get("mapType").equals(mapType)) {
          matchingUsers.add(userStatus);
        }
      } catch (JsonProcessingException e) {
        System.err.println("JSON 파싱 중 오류 발생: " + e.getMessage());
      }
    }

    return matchingUsers;
  }


}
