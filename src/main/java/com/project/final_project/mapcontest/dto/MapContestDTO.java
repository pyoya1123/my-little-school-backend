package com.project.final_project.mapcontest.dto;

import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.mapcontest.domain.MapContest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MapContestDTO {
  private Integer id;
  private String title;
  private String description;
  private List<MapContestFurnitureVoDTO> furnitureList;
  private String previewImageUrl;
  private Integer likeCount = 0;
  private Integer viewCount = 0;
  private Integer userId;
  private String publicId;

  public MapContestDTO(MapContest m){
    this.id = m.getId();
    this.title= m.getTitle();
    this.description = m.getDescription();
    this.furnitureList = m.getFurnitureList();
    this.previewImageUrl = m.getPreviewImageUrl();
    this.likeCount = m.getLikeCount();
    this.viewCount = m.getViewCount();
    this.userId = m.getUserId();
    this.publicId = m.getPublicId();
  }
}
