package com.project.final_project.mapcontest.domain;

import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "map_contest")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapContest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @ElementCollection
  @CollectionTable(
      name = "map_contest_furniture_list",
      joinColumns = @JoinColumn(name = "map_contest_id")
  )
  private List<MapContestFurnitureVoDTO> furnitureList;

  @Column(name = "preview_image_url")
  private String previewImageUrl;

  @Column(name = "like_count")
  private Integer likeCount = 0;

  @Column(name = "view_count")
  private Integer viewCount = 0;

  @Column(name = "public_id")
  private String publicId;

  public MapContest(Integer userId, String title, String description,
      List<MapContestFurnitureVoDTO> furnitureList, String previewImageUrl, Integer likeCount,
      Integer viewCount, String publicId) {
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.furnitureList = furnitureList;
    this.previewImageUrl = previewImageUrl;
    this.likeCount = likeCount;
    this.viewCount = viewCount;
    this.publicId = publicId;
  }
}
