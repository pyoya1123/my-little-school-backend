package com.project.final_project.gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryGalleryRegisterDTO {

  String publicId;
  String imgUrl;
  String title;
  Integer schoolId;
  Integer userId;
}
