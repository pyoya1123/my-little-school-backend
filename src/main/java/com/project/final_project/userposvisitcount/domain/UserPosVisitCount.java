package com.project.final_project.userposvisitcount.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_pos_visit_count")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPosVisitCount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "map_type")
  private String mapType;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "user_pos_visit_count")
  private Integer count;

  public UserPosVisitCount(String mapType, Integer userId, Integer count) {
    this.mapType = mapType;
    this.userId = userId;
    this.count = count;
  }
}
