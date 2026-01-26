package com.project.final_project.mapcontest.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class MapContestFurnitureVoDTO {

  @Column(name = "obj_id")
  private Integer objId;

  @Column(name = "x")
  private Integer x;

  @Column(name = "y")
  private Integer y;

  @Column(name = "rotation")
  private Integer rot;

  @Column(name = "flip")
  private Boolean flip;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "map_type")
  private String mapType;

  public MapContestFurnitureVoDTO(Integer objId, Integer x, Integer y, Integer rot, Boolean flip,
      Integer userId, String mapType) {
    this.objId = objId;
    this.x = x;
    this.y = y;
    this.rot = rot;
    this.flip = flip;
    this.userId = userId;
    this.mapType = mapType;
  }
}
