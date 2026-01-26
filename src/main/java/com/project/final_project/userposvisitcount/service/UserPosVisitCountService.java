package com.project.final_project.userposvisitcount.service;

import com.project.final_project.userposvisitcount.domain.UserPosVisitCount;
import com.project.final_project.userposvisitcount.dto.UserPosVisitCountDTO;
import com.project.final_project.userposvisitcount.repository.UserPosVisitCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPosVisitCountService {

  private final UserPosVisitCountRepository userPosVisitCountRepository;

  public UserPosVisitCountDTO getUserPosVisitCountByMapIdAndMapTypeAndUserId(
      String mapType, int userId) {
    UserPosVisitCount userPosVisitCount = userPosVisitCountRepository
        .getUserPosVisitCountByMapIdAndMapTypeAndUserId(mapType, userId);

    if(userPosVisitCount == null) {
      userPosVisitCount = userPosVisitCountRepository
          .save(new UserPosVisitCount(mapType, userId, 0));
    }

    return new UserPosVisitCountDTO(userPosVisitCount);
  }

  @Transactional
  public UserPosVisitCountDTO updateUserPosVisitCountByMapIdAndMapTypeAndUserId(
      String mapType, int userId) {

    UserPosVisitCount userPosVisitCount = userPosVisitCountRepository
        .getUserPosVisitCountByMapIdAndMapTypeAndUserId(mapType, userId);

    if(userPosVisitCount == null) {
      userPosVisitCount = userPosVisitCountRepository
          .save(new UserPosVisitCount(mapType, userId, 0));
    }
    else {
      userPosVisitCount.setCount(userPosVisitCount.getCount() + 1);
    }

    return new UserPosVisitCountDTO(userPosVisitCount);
  }

  @Transactional
  public void deleteUserPosVisitCountListByUserId(Integer userId) {
    userPosVisitCountRepository.deleteAll(
        userPosVisitCountRepository.getUserPosVisitCountListByUserId(userId));
  }
}
