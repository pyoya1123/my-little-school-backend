package com.project.final_project.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.friendship.dto.FriendshipResponseDTO;
import com.project.final_project.friendship.service.FriendshipService;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.repository.UserRepository;
import com.project.final_project.websocket.repository.UserStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final FriendshipService friendshipService;


  public void updateUserStatus(Integer userId, String response) {
    try {
      // 유저 상태 업데이트 처리 (예: 브로드캐스트 또는 저장)
      userStatusRepository.setUserStatus(userId, response);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to update user status for userId: " + userId);
    }
  }

  public void removeUser(Integer userId) {
    userStatusRepository.removeUser(userId);
  }

  public Map<Integer, String> getAllStatuses() {
    return userStatusRepository.getAllStatuses();
  }

  public List<FriendshipResponseDTO> getAllFriendStatuses(Integer userId) {
    return friendshipService.getAllAcceptedFriendships(userId);
  }

  public String getUserStatus(Integer userId) {
    return userStatusRepository.getStatus(userId);
  }

  public List<Map<String, Object>> getUserStatusByMapIdAndMapType(Integer mapId, String mapType){
    return userStatusRepository.getUserStatusByMapIdAndMapType(mapId, mapType);
  }

  public Integer getUserCountInSchool(Integer mapId, String mapType) {
    return getUserStatusByMapIdAndMapType(mapId, mapType).size();
  }

}
