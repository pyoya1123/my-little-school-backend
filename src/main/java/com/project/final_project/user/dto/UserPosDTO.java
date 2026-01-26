package com.project.final_project.user.dto;

import com.project.final_project.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPosDTO {

  private Integer userId;
  private Integer mapId;
  private String mapType;

  public UserPosDTO(User user) {
    this.userId = user.getId();
    this.mapId = user.getMapId();
    this.mapType = user.getMapType();
  }

}
