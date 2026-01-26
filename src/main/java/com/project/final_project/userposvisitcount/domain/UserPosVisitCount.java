package com.project.final_project.userposvisitcount.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @Column(name = "user_pos_visit_count")
  private Integer count;

  public UserPosVisitCount(String mapType, User user, Integer count) {
    this.mapType = mapType;
    this.user = user;
    this.count = count;
  }

  /**
   * 기존 코드 호환성을 위한 생성자 (deprecated)
   * @deprecated User 객체를 직접 사용하는 생성자를 사용하세요
   */
  @Deprecated
  public UserPosVisitCount(String mapType, Integer userId, Integer count) {
    this.mapType = mapType;
    this.count = count;
    // userId는 나중에 User 객체로 설정해야 함
  }
}
