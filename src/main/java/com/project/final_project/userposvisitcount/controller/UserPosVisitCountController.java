package com.project.final_project.userposvisitcount.controller;

import com.project.final_project.userposvisitcount.domain.UserPosVisitCount;
import com.project.final_project.userposvisitcount.dto.UserPosVisitCountDTO;
import com.project.final_project.userposvisitcount.service.UserPosVisitCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-pos-visit-count")
public class UserPosVisitCountController {

  private final UserPosVisitCountService userPosVisitCountService;

  @GetMapping("/{mapType}/{userId}")
  public UserPosVisitCountDTO getUserPosVisitCountByMapIdAndMapTypeAndUserId(
      @PathVariable("mapType") String mapType,
      @PathVariable("userId") int userId) {
    return userPosVisitCountService
        .getUserPosVisitCountByMapIdAndMapTypeAndUserId(mapType, userId);
  }

  @PatchMapping("/{mapType}/{userId}")
  public UserPosVisitCountDTO updateUserPosVisitCountByMapIdAndMapTypeAndUserId(
      @PathVariable("mapType") String mapType,
      @PathVariable("userId") int userId) {
    return userPosVisitCountService
        .updateUserPosVisitCountByMapIdAndMapTypeAndUserId(mapType, userId);
  }
}
