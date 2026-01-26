package com.project.final_project.userposvisitcount.dto;

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
}
