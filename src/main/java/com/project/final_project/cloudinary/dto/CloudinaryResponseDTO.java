package com.project.final_project.cloudinary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryResponseDTO {

  private String url;
  private String publicId;

}
