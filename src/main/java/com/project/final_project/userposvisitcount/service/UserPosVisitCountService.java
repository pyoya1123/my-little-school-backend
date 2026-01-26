package com.project.final_project.userposvisitcount.service;

import com.project.final_project.user.domain.User;
import com.project.final_project.user.repository.UserRepository;
import com.project.final_project.userposvisitcount.dto.UserPosVisitCountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserPosVisitCount 서비스
 * User 엔티티의 visitCounts 필드를 관리합니다.
 */
@Service
@RequiredArgsConstructor
public class UserPosVisitCountService {

  private final UserRepository userRepository;

  /**
   * User와 mapType으로 방문 횟수를 조회합니다.
   * 없으면 0을 반환합니다.
   */
  @Transactional(readOnly = true)
  public UserPosVisitCountDTO getUserPosVisitCountByMapIdAndMapTypeAndUserId(
      String mapType, int userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    Integer count = user.getVisitCount(mapType);
    return new UserPosVisitCountDTO(mapType, userId, count);
  }

  /**
   * 방문 횟수를 1 증가시킵니다.
   */
  @Transactional
  public UserPosVisitCountDTO updateUserPosVisitCountByMapIdAndMapTypeAndUserId(
      String mapType, int userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // 헬퍼 메서드를 사용하여 방문 횟수 증가
    user.incrementVisitCount(mapType);
    
    // 변경사항 저장
    userRepository.save(user);

    return new UserPosVisitCountDTO(mapType, userId, user.getVisitCount(mapType));
  }

  /**
   * User의 모든 방문 횟수 데이터를 삭제합니다.
   * User 삭제 시 자동으로 삭제되지만, 명시적으로 호출 가능합니다.
   */
  @Transactional
  public void deleteUserPosVisitCountListByUserId(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    
    user.clearVisitCounts();
    userRepository.save(user);
  }
}
