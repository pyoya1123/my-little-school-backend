package com.project.final_project.gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryGalleryUpdateDTO {
  Integer galleryId;
  String imgUrl;
  String title;
  Integer schoolId;
  String enteredDate;
  String publicId;
}
