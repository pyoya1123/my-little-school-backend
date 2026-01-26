package com.project.final_project.schedule.service;

import com.project.final_project.user.dto.UserRequestOnlineStatusDTO;
import com.project.final_project.user.service.UserService;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
  private ScheduledFuture<?> scheduledFuture;
  private UserService userService;

  // Map<mapId-mapType, Set<userId>> 구조로 관리
  private Map<String, Set<Integer>> prev_onlineUsersByMap = new HashMap<>();
  private final Map<String, Set<Integer>> onlineUsersByMap = new HashMap<>();

  @Autowired
  public ScheduleService(UserService userService) {
    this.userService = userService;
  }


  // 60초 타이머 시작
  public void startTimer() {
    scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
      try {
        // 타이머가 끝날 때마다 유저 상태 초기화
        userService.setAllUserStatusToOffline();

        prev_onlineUsersByMap = deepCopyMap(onlineUsersByMap);

        // 온라인 상태 유저 초기화
        onlineUsersByMap.clear();

        // 맵에 있는 유저들 온라인 상태로 세팅
        setUserStatus();

      } catch (Exception e) {
        System.err.println("Scheduled task error: " + e.getMessage());
        e.printStackTrace();
      }
    }, 0, 5, TimeUnit.SECONDS); // 60초 타이머
  }

  private Map<String, Set<Integer>> deepCopyMap(Map<String, Set<Integer>> original) {
    Map<String, Set<Integer>> copy = new HashMap<>();
    original.forEach((key, valueSet) -> copy.put(key, new HashSet<>(valueSet)));
//    System.out.println(original.size() + ", " + copy.size());
    return copy;
  }

  void setUserStatus() {
    prev_onlineUsersByMap.forEach((key, userIds) -> {
      for (Integer userId : userIds) {
        userService.setUserStatusToOnline(userId);
      }
    });
  }

  // 특정 유저 상태를 온라인으로 설정
  public synchronized void addUserStatusToMap(UserRequestOnlineStatusDTO dto) {
    // mapId와 mapType을 키로 조합하여 사용
    String mapKey = dto.getMapId() + "-" + dto.getMapType();

    // 해당 키가 없으면 새로 생성
    onlineUsersByMap.putIfAbsent(mapKey, new HashSet<>());
    onlineUsersByMap.get(mapKey).add(dto.getUserId());

    userService.setUserStatusToOnline(dto.getUserId());
  }

  // 특정 mapId와 mapType에 해당하는 유저 수를 반환하는 메서드
  public synchronized int getUserCountByMap(Integer mapId, String mapType) {
    String mapKey = mapId + "-" + mapType;
    return onlineUsersByMap.getOrDefault(mapKey, new HashSet<>()).size();
  }

  @PostConstruct
  public void init() {
//    startTimer();
  }
}
