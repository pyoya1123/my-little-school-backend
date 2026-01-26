package com.project.final_project.userposvisitcount.dto;


import com.project.final_project.userposvisitcount.domain.UserPosVisitCount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPosVisitCountDTO {
  private String mapType;
  private Integer userId;
  private Integer count;

  public UserPosVisitCountDTO(UserPosVisitCount upvc) {
    this.mapType = upvc.getMapType();
    this.userId = upvc.getUserId();
    this.count = upvc.getCount();
  }
}
