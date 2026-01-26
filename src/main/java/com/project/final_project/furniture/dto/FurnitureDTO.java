package com.project.final_project.furniture.dto;

import com.project.final_project.furniture.domain.Furniture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureDTO {
  private Integer id;
  private Integer objId;
  private Integer x;
  private Integer y;
  private Integer rot;
  private Boolean flip;
  private Integer mapId;
  private String mapType;

  public FurnitureDTO(Furniture furniture){
    this.id = furniture.getId();
    this.objId = furniture.getObjId();
    this.x = furniture.getX();
    this.y = furniture.getY();
    this.rot = furniture.getRot();
    this.flip = furniture.getFlip();
    this.mapId = furniture.getMapId();
    this.mapType = furniture.getMapType();
  }



}
