package com.project.final_project.mapcontest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapContestRegisterDTO {
  private String title;
  private String description;
  private String previewImageUrl;
  private Integer userId;
  private String publicId;
}
