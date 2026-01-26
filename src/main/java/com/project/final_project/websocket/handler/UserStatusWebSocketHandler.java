package com.project.final_project.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.final_project.common.util.QueryParamUtil;
import com.project.final_project.friendship.domain.Friendship;
import com.project.final_project.friendship.dto.FriendshipRequestDTO;
import com.project.final_project.friendship.dto.FriendshipResponseDTO;
import com.project.final_project.friendship.repository.FriendshipRepository;
import com.project.final_project.friendship.service.FriendshipService;
import com.project.final_project.user.dto.UserPosDTO;
import com.project.final_project.user.dto.UserPosUpdateDTO;
import com.project.final_project.user.service.UserService;
import com.project.final_project.websocket.manager.UserSessionManager;
import com.project.final_project.websocket.service.UserStatusService;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class UserStatusWebSocketHandler extends TextWebSocketHandler {

  @Autowired
  private UserStatusService userStatusService; // 유저 상태 저장소
  @Autowired
  private ObjectMapper jacksonObjectMapper;
  @Autowired
  private UserService userService;
  @Autowired
  private FriendshipService friendshipService;
  @Autowired
  private UserSessionManager userSessionManager;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    Map<String, Object> messageMap = jacksonObjectMapper.readValue(payload, Map.class);

    String messageType = (String) messageMap.get("type");
    switch (messageType) {
      case "FETCH_PENDING_REQUESTS": // 수락되지 않은 친구 요청 목록 조회 (수신자 기준)
        handleFetchPendingRequests(session, messageMap);
        break;
      case "FETCH_PENDING_REQUESTS_BY_REQUESTER":  // 수락되지 않은 친구 요청 목록 조회 (요청자 기준)
        handleFetchPendingRequestsByRequester(session, messageMap);
        break;
      case "FRIEND_REQUEST": // 친구 요청
        handleFriendRequest(messageMap);
        break;
      case "FRIEND_REJECT":  // 친구 요청 거절
        handleFriendReject(messageMap);
        break;
      case "FRIEND_ACCEPT":  // 친구 요청 수락
        handleFriendAccept(messageMap);
        break;
      case "FETCH_FRIEND_LIST":  // 모든 친구 조회
        handleFetchFriendList(session, messageMap);
        break;
      case "DELETE_FRIEND_REQUEST":  // 친구 요청 삭제
        handleDeleteFriendRequest(messageMap);
        break;
      case "DELETE_FRIENDSHIP": // 친구 관계 삭제
        handleDeleteFriendship(messageMap);
        break;
      case "USER_POS_INFO":
        handleUserPosInfo(messageMap);
        break;
      case "GET_USER_POS":
        handleGetUserInfo(session, messageMap);
        break;
      default:
        session.sendMessage(new TextMessage("Unknown message type: " + messageType));
    }
  }

  private void handleGetUserInfo(WebSocketSession session, Map<String, Object> messageMap) throws IOException {
    Integer userId = (Integer) messageMap.get("userId");

    UserPosDTO pos = userService.getPosition(userId);

    Map<String, Object> userPosResponse = new HashMap<>();
    userPosResponse.put("type", "ACCEPT_USER_POS");
    userPosResponse.put("userId", userId);
    userPosResponse.put("mapId", pos.getMapId());
    userPosResponse.put("mapType", pos.getMapType());

    String userPosJsonResponse = jacksonObjectMapper.writeValueAsString(userPosResponse);
    session.sendMessage(new TextMessage(userPosJsonResponse));
  }

  // 클라이언트가 맵 이동 시, 맵 이동 정보를 모든 유저한테 브로드캐스팅함. (userId, mapId, mapType, isOnline)
  private void handleUserPosInfo(Map<String, Object> messageMap) throws IOException {
    Integer userId = (Integer) messageMap.get("userId");
    Integer mapId = (Integer) messageMap.get("mapId");
    String mapType = (String) messageMap.get("mapType");
    Boolean isOnline = true;

    userService.updatePosition(new UserPosUpdateDTO(userId, mapId, mapType));

    String userStatusJsonResponse = jacksonObjectMapper.writeValueAsString(messageMap);
    userStatusService.updateUserStatus(userId, userStatusJsonResponse);


    Map<String, Object> friendsResponse = new HashMap<>();
    friendsResponse.put("type", "ACCEPT_FRIEND_POS_INFO");
    friendsResponse.put("userId", userId);
    friendsResponse.put("mapId", mapId);
    friendsResponse.put("mapType", mapType);
    friendsResponse.put("isOnline", isOnline);

    String friendsJsonResponse = jacksonObjectMapper.writeValueAsString(friendsResponse);

    // 모든 연결된 세션에 상태 전송
    userSessionManager.getFriendsSessions(userId).values().forEach(session -> {
      try {
        session.sendMessage(new TextMessage(friendsJsonResponse));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void handleDeleteFriendship(Map<String, Object> messageMap)
      throws IOException {
    Integer friendshipId = (Integer) messageMap.get("friendshipId");
    Friendship friendship = friendshipService.getFriendshipById(friendshipId);
    friendshipService.removeFriendship(friendshipId);

    // 응답 메시지 생성
    Map<String, Object> requester_response = new HashMap<>();
    requester_response.put("type", "COMPLETE_DELETE_FRIENDSHIP");
    requester_response.put("message", "아이디 : " +  friendshipId + " 인 친구 관계를 삭제했습니다.");

    Map<String, Object> receiver_response = new HashMap<>();
    receiver_response.put("type", "NOTIFICATION_DELETE_FRIENDSHIP");
    receiver_response.put("message", "아이디 : " + friendshipId + " 인 친구 관계가 삭제되었습니다");

    String requester_jsonResponse = jacksonObjectMapper.writeValueAsString(requester_response);
    WebSocketSession requesterSession = UserSessionManager.getSession(friendship.getRequesterId());
    if(requesterSession != null) {
      requesterSession.sendMessage(new TextMessage(requester_jsonResponse));
    }

    String receiver_jsonResponse = jacksonObjectMapper.writeValueAsString(receiver_response);
    WebSocketSession receiverSession = UserSessionManager.getSession(friendship.getReceiverId());
    if(receiverSession != null) {
      receiverSession.sendMessage(new TextMessage(receiver_jsonResponse));
    }
  }

  private void handleFetchPendingRequestsByRequester(WebSocketSession session, Map<String, Object> messageMap)
      throws IOException {
    Integer userId = (Integer) messageMap.get("userId");
    List<FriendshipResponseDTO> pendingRequests = friendshipService.getUnacceptedFriendshipListByRequester(userId);

    // 응답 메시지 생성
    Map<String, Object> response = new HashMap<>();
    response.put("type", "PENDING_REQUESTS_BY_REQUESTER");
    response.put("requests", pendingRequests);

    String jsonResponse = jacksonObjectMapper.writeValueAsString(response);
    session.sendMessage(new TextMessage(jsonResponse));
  }

  private void handleDeleteFriendRequest(Map<String, Object> messageMap)
      throws IOException {
    Integer friendshipId = (Integer) messageMap.get("friendshipId");
    Friendship friendship = friendshipService.getFriendshipById(friendshipId);
    friendshipService.removeFriendship(friendshipId);

    // 응답 메시지 생성
    Map<String, Object> requester_response = new HashMap<>();
    requester_response.put("type", "COMPLETE_DELETE_FRIEND_REQUEST");
    requester_response.put("message", "아이디 : " +  friendshipId + " 인 친구 요청을 삭제했습니다.");

    Map<String, Object> receiver_response = new HashMap<>();
    receiver_response.put("type", "NOTIFICATION_DELETE_FRIEND_REQUEST");
    receiver_response.put("message", "아이디 : " + friendshipId + " 인 친구 요청이 삭제되었습니다");

    String requester_jsonResponse = jacksonObjectMapper.writeValueAsString(requester_response);
    WebSocketSession requesterSession = UserSessionManager.getSession(friendship.getRequesterId());
    if(requesterSession != null) {
      requesterSession.sendMessage(new TextMessage(requester_jsonResponse));
    }

    String receiver_jsonResponse = jacksonObjectMapper.writeValueAsString(receiver_response);
    WebSocketSession receiverSession = UserSessionManager.getSession(friendship.getReceiverId());
    if(receiverSession != null) {
      receiverSession.sendMessage(new TextMessage(receiver_jsonResponse));
    }
  }


  //== 친구 요청을 받은 친구 입장에서, 친구 요청이 보류중인 요청들 관리 ==//
  private void handleFetchPendingRequests(WebSocketSession session, Map<String, Object> messageMap) throws Exception {
    Integer userId = (Integer) messageMap.get("userId");
    List<FriendshipResponseDTO> pendingRequests = friendshipService.getUnacceptedFriendshipListByReceiver(userId);

    // 응답 메시지 생성
    Map<String, Object> response = new HashMap<>();
    response.put("type", "PENDING_REQUESTS");
    response.put("requests", pendingRequests);

    String jsonResponse = jacksonObjectMapper.writeValueAsString(response);
    session.sendMessage(new TextMessage(jsonResponse));
  }


  //== 친구 요청 관리 ==//
  private void handleFriendRequest(Map<String, Object> messageMap) throws Exception {
    Integer requesterId = (Integer) messageMap.get("requesterId");
    Integer receiverId = (Integer) messageMap.get("receiverId");
    String requestMessage = (String) messageMap.get("message");

    try {
      // 친구 요청 생성
      friendshipService.sendFriendRequest(
          new FriendshipRequestDTO(requesterId, receiverId, requestMessage));
      
      broadcastPendingRequests(requesterId, receiverId);
    } catch (IllegalArgumentException e) {
      // 이미 요청된 친구 요청에 대한 예외 처리
      sendErrorMessage(requesterId, e.getMessage());
    } catch (Exception e) {
      // 기타 예외 처리
      sendErrorMessage(requesterId, "친구 요청 중 알 수 없는 오류가 발생했습니다.");
      e.printStackTrace();
    }
  }


  //== 친구 요청 거절 ==//
  private void handleFriendReject(Map<String, Object> messageMap) throws Exception {
    Integer friendshipId = (Integer) messageMap.get("friendshipId");
    Friendship friendship = friendshipService.getFriendshipById(friendshipId);
    friendshipService.removeFriendship(friendshipId);

    // 알림 전송
    WebSocketSession requesterSession = UserSessionManager.getSession(friendship.getRequesterId());
    if (requesterSession != null) {
      requesterSession.sendMessage(new TextMessage("친구 요청이 거절되었습니다."));
    }

    // 알림 전송
    WebSocketSession receiverSession = UserSessionManager.getSession(friendship.getReceiverId());
    if (receiverSession != null) {
      receiverSession.sendMessage(new TextMessage("친구 요청이 거절되었습니다."));
    }
  }


  //== 친구 요청 수락 ==//
  private void handleFriendAccept(Map<String, Object> messageMap) throws Exception {
    Integer friendshipId = (Integer) messageMap.get("friendshipId");

    // 친구 요청 수락 처리
    friendshipService.acceptFriendRequest(friendshipId);

    // 요청자에게 알림 전송
    Friendship friendship = friendshipService.getFriendshipById(friendshipId);
    Integer requesterId = friendship.getRequesterId();
    Integer receiverId = friendship.getReceiverId();

    Map<String, Object> statusUpdateToRequester = new HashMap<>();
    statusUpdateToRequester.put("type", "ACCEPT_FRIENDSHIP_REQUEST");
    statusUpdateToRequester.put("message", "아이디 : " + receiverId + "인 유저가 친구 요청을 수락했습니다.");

    Map<String, Object> statusUpdateToReceiver = new HashMap<>();
    statusUpdateToReceiver.put("type", "ACCEPT_FRIENDSHIP_REQUEST_CALLBACK");
    statusUpdateToReceiver.put("message", "아이디 : " + requesterId + "인 유저의 친구 요청을 수락합니다.");

    String requester_jsonResponse = jacksonObjectMapper.writeValueAsString(statusUpdateToRequester);
    String receiver_jsonResponse = jacksonObjectMapper.writeValueAsString(statusUpdateToReceiver);

    sendNotification(requesterId, requester_jsonResponse);
    sendNotification(receiverId, receiver_jsonResponse);
  }


  private void handleFetchFriendList(WebSocketSession session, Map<String, Object> messageMap) throws Exception {
    Integer userId = (Integer) messageMap.get("userId");
    List<FriendshipResponseDTO> friends = friendshipService.getAllAcceptedFriendships(userId);

    // 응답 메시지 생성
    Map<String, Object> response = new HashMap<>();
    response.put("type", "FRIEND_LIST");
    response.put("friends", friends);

    // 친구 목록 전송
    String jsonResponse = jacksonObjectMapper.writeValueAsString(response);
    session.sendMessage(new TextMessage(jsonResponse));
  }


  //== 친구 요청이 왔다는 알림 보내기 ==//
  private void sendNotification(Integer userId, String message) {
    WebSocketSession userSession = UserSessionManager.getSession(userId);
    if (userSession != null && userSession.isOpen()) {
      try {
        userSession.sendMessage(new TextMessage(message));
        System.out.println("Notification sent to userId: " + userId);
      } catch (Exception e) {
        System.out.println("Failed to send notification to userId: " + userId);
        e.printStackTrace();
      }
    } else {
      System.out.println("Session not found or closed for userId: " + userId);
    }
  }

  private void sendErrorMessage(Integer userId, String errorMessage) {
    WebSocketSession userSession = UserSessionManager.getSession(userId);
    if (userSession != null && userSession.isOpen()) {
      try {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "ERROR");
        response.put("message", errorMessage);

        String jsonResponse = jacksonObjectMapper.writeValueAsString(response);
        userSession.sendMessage(new TextMessage(jsonResponse));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // ex) ws://localhost:5544/ws?userId=1
    URI uri = session.getUri(); // 웹 소켓 세션의 URI 가져오기
    if (uri != null) {
      Map<String, String> queryParams = QueryParamUtil.getQueryParameters(uri);
      Integer userId = Integer.valueOf(queryParams.get("userId"));
      Integer mapId = Integer.valueOf(queryParams.get("mapId"));
      String mapType = String.valueOf(queryParams.get("mapType"));

      if (userId != null) {
        // 사용자 세션 등록
        UserSessionManager.registerSession(userId, session);

        // JSON 형식으로 사용자 상태 업데이트
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("type", "CHECK_USER_ONLINE_STATUS");
        statusUpdate.put("userId", userId);
        statusUpdate.put("mapId", mapId);
        statusUpdate.put("mapType", mapType);
        statusUpdate.put("status", "online");

        // JSON 문자열 생성
        String jsonResponse = jacksonObjectMapper.writeValueAsString(statusUpdate);

        // 사용자 상태 업데이트
        userStatusService.updateUserStatus(userId, jsonResponse);

        // 사용자 상태를 데이터베이스에 반영
        userService.setUserStatusToOnline(userId);
        userService.updatePosition(new UserPosUpdateDTO(userId, mapId, mapType));

        // 연결된 모든 유저에게 상태 변경 브로드캐스트
        broadcastUserStatuses(userId);
      }
      else {
        sendErrorMessage(userId, "해당 유저 아이디인 유저가 존재하지 않습니다.");
      }
    }
  }


  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    // 연결이 종료된 유저 ID 제거
    Integer userId = UserSessionManager.getAllSessions().entrySet().stream()
        .filter(entry -> entry.getValue().equals(session))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);

    if (userId != null) {
      UserSessionManager.removeSession(userId);
      userStatusService.removeUser(userId);
      userService.setUserStatusToOffline(userId);

      // 변경된 유저 목록 브로드캐스트
      broadcastUserStatuses(userId);
    }
  }


  //== 유저가 온라인 상태라면 현재 세션에 연결된 모든 유저한테 브로드캐스팅 ==//
  private void broadcastUserStatuses(Integer userId) {
    // 모든 유저의 상태를 JSON 형태로 생성
    String userStatusesJson = userStatusService.getUserStatus(userId);
    System.out.println("userStatusesJson = " + userStatusesJson);

    // 모든 연결된 친구 세션한테 브로드캐스팅
    userSessionManager.getFriendsSessions(userId).values().forEach(session -> {
      try {
        session.sendMessage(new TextMessage(userStatusesJson));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  //== 친구 요청 기능 ==//
  //== a가 b한테 친구 요청을 했을 때, b한테 친구 요청 보류 상태를 브로드캐스팅함 ==//`
  private void broadcastPendingRequests(Integer requesterId, Integer receiverId) {
    List<FriendshipResponseDTO> pendingRequestsByReceiver = friendshipService.getUnacceptedFriendshipListByReceiver(receiverId);
    List<FriendshipResponseDTO> pendingRequestsByRequester = friendshipService.getUnacceptedFriendshipListByRequester(requesterId);

    WebSocketSession receiverSession = UserSessionManager.getSession(receiverId);
    WebSocketSession requesterSession = UserSessionManager.getSession(requesterId);

    if (receiverSession != null && receiverSession.isOpen()) {
      try {
        Map<String, Object> receiverResponse = new HashMap<>();
        Map<String, Object> requesterResponse = new HashMap<>();

        receiverResponse.put("type", "PENDING_REQUESTS");
        receiverResponse.put("requests", pendingRequestsByReceiver);

        // 요청한 사람에게 친구 요청 보류 리스트 전송
        requesterResponse.put("type", "PENDING_REQUESTER_REQUESTS");
        requesterResponse.put("requests", pendingRequestsByRequester);

        String jsonReceiverResponse = jacksonObjectMapper.writeValueAsString(receiverResponse);
        receiverSession.sendMessage(new TextMessage(jsonReceiverResponse));

        String jsonRequesterResponse = jacksonObjectMapper.writeValueAsString(requesterResponse);
        requesterSession.sendMessage(new TextMessage(jsonRequesterResponse));

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String toJson(String data) {
    try {
      // String 값을 그대로 JSON으로 반환
      return jacksonObjectMapper.writeValueAsString(data);
    } catch (Exception e) {
      // 오류 발생 시 기본 빈 JSON 반환 및 로그 출력
      e.printStackTrace();
      return "{}";
    }
  }
}
