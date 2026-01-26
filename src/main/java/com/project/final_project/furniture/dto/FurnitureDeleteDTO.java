package com.project.final_project.furniture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureDeleteDTO {

  Integer objectId;
  Integer removedCount;

  @Override
  public String toString() {
    return "FurnitureDeleteDTO{" +
        "objectId=" + objectId +
        ", removedCount=" + removedCount +
        '}';
  }
}
