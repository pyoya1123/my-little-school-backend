package com.project.final_project.websocket.manager;

import com.project.final_project.friendship.dto.FriendshipResponseDTO;
import com.project.final_project.friendship.service.FriendshipService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionManager {

  // 사용자 ID와 WebSocketSession을 매핑
  private static final ConcurrentHashMap<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

  @Autowired
  private FriendshipService friendshipService;

  /**
   * 사용자 ID와 WebSocket 세션을 등록합니다.
   */
  public static void registerSession(Integer userId, WebSocketSession session) {
    userSessions.put(userId, session);
  }

  /**
   * 사용자 ID에 해당하는 WebSocket 세션을 제거합니다.
   */
  public static void removeSession(Integer userId) {
    userSessions.remove(userId);
  }

  /**
   * 사용자 ID에 해당하는 WebSocket 세션을 반환합니다.
   */
  public static WebSocketSession getSession(Integer userId) {
    return userSessions.get(userId);
  }

  /**
   * 모든 세션을 반환합니다.
   */
  public static ConcurrentHashMap<Integer, WebSocketSession> getAllSessions() {
    return userSessions;
  }

  public ConcurrentHashMap<Integer, WebSocketSession> getFriendsSessions(Integer userId) {
    // 승인된 친구 목록을 가져옵니다.
    List<FriendshipResponseDTO> friends = friendshipService.getAllAcceptedFriendships(userId);

    // 친구들의 세션 정보를 저장할 ConcurrentHashMap 생성
    ConcurrentHashMap<Integer, WebSocketSession> friendsSessions = new ConcurrentHashMap<>();

    // 친구 목록을 순회하면서 세션 정보를 추가
    for (FriendshipResponseDTO friend : friends) {
      // 친구 중 사용자 ID가 requester일 경우 receiver의 세션을 저장
      if (friend.getRequester().getId().equals(userId)) {
        WebSocketSession session = UserSessionManager.getSession(friend.getReceiver().getId());
        if (session != null) {
          friendsSessions.put(friend.getReceiver().getId(), session);
        }
      }
      // 친구 중 사용자 ID가 receiver일 경우 requester의 세션을 저장
      else if (friend.getReceiver().getId().equals(userId)) {
        WebSocketSession session = UserSessionManager.getSession(friend.getRequester().getId());
        if (session != null) {
          friendsSessions.put(friend.getRequester().getId(), session);
        }
      }
    }

    // 친구들의 세션 정보가 저장된 ConcurrentHashMap 반환
    return friendsSessions;
  }

}