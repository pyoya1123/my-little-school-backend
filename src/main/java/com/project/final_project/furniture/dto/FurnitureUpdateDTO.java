package com.project.final_project.furniture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FurnitureUpdateDTO {
  private Integer id;
  private Integer objId;
  private Integer x;
  private Integer y;
  private Integer rot;
  private Boolean flip;
  private Integer mapId;
  private String mapType;
}
