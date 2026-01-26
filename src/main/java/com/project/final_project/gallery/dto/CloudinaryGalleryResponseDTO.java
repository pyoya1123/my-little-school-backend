package com.project.final_project.gallery.dto;

import com.project.final_project.gallery.domain.Gallery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryGalleryResponseDTO {
  Integer id;
  String imgUrl;
  String title;
  Integer schoolId;
  String enteredDate;
  String publicId;

  public CloudinaryGalleryResponseDTO(Gallery gallery) {
    this.id = gallery.getId();
    this.imgUrl = gallery.getImgUrl();
    this.title = gallery.getTitle();
    this.schoolId = gallery.getSchoolId();
    this.enteredDate = gallery.getEnteredDate();
    this.publicId = gallery.getPublicId();
  }
}
