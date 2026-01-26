package com.project.final_project.gallery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gallery")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Gallery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "image_base_64")
  String imgUrl;

  @Column(name = "title")
  String title;

  @Column(name = "school_id")
  Integer schoolId;

  @Column(name = "entered_date")
  String enteredDate;

  @Column(name = "user_id")
  Integer userId;

  //== cloudinary ==//
  @Column(name = "public_id")
  String publicId;

  public Gallery(String imgUrl, String title, Integer schoolId, Integer userId, String publicId) {
    this.imgUrl = imgUrl;
    this.title = title;
    this.schoolId = schoolId;
    this.userId = userId;
    this.publicId = publicId;
  }

  // 자동으로 enteredDate 설정
  @PrePersist
  protected void onCreate() {
    this.enteredDate = LocalDateTime.now().toString(); // 현재 시간 저장
  }
}
