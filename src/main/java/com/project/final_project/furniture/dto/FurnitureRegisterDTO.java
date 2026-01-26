package com.project.final_project.furniture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureRegisterDTO {
  private Integer objId;
  private Integer x;
  private Integer y;
  private Integer rot;
  private Boolean flip;
  private Integer mapId;
  private String mapType;
  private Integer userId;
}
