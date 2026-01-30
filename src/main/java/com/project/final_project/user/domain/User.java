package com.project.final_project.user.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.guestbook.domain.GuestBook;
import com.project.final_project.quest.domain.UserQuest;
import com.project.final_project.school.domain.School;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  Integer id;

  @Column(name = "user_name")
  String name;

  @Column(name = "user_grade")
  Integer grade;

  @Column(name = "user_birthday")
  String birthday;

  @Column(name = "user_gender")
  Boolean gender;

  @Column(name = "user_email")
  String email;

  @Column(name = "user_password")
  String password;

  @Column(name = "user_phone")
  String phone;

  @Column(name = "user_level")
  Integer level;

  @Column(name = "user_exp")
  Integer exp;

  @Column(name = "user_max_exp")
  Integer maxExp;

  @Column(name = "user_status_message")
  String statusMessage;

  @Column(name ="user_gold")
  Integer gold;

//  @ElementCollection(fetch = FetchType.EAGER)
//  @Column(name = "interest")
//  List<String> interest = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "school_id")
  @JsonIgnore
  School school;

  @Column(name = "is_online")
  Boolean isOnline;

  @Column(name = "entered_date")
  String enteredDate;

  @Column(name = "map_id")
  Integer mapId;

  @Column(name = "map_type")
  String mapType;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<UserQuest> userQuests;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<GuestBook> guestBooks;

  //==위치별 방문 횟수 (mapType -> count) ==//
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "user_pos_visit_count", 
                   joinColumns = @JoinColumn(name = "user_id"))
  @MapKeyColumn(name = "map_type")
  @Column(name = "user_pos_visit_count")
  @JsonIgnore
  private Map<String, Integer> visitCounts = new HashMap<>();

  //==비즈니스 로직==//
  public void gainExp(Integer exp) {
    this.exp += exp;
    while(this.exp >= this.maxExp){
      levelUp();
    }
  }

  private void levelUp() {
    this.exp -= this.maxExp;
    this.level += 1;
    this.maxExp = calculateMaxExpForNextLevel(this.level);
  }

  private Integer calculateMaxExpForNextLevel(Integer level) {
    return com.project.final_project.common.constants.UserConstants.BASE_EXP 
        + (level - 1) * com.project.final_project.common.constants.UserConstants.EXP_INCREMENT_PER_LEVEL;
  }

  //==School 연관관계 헬퍼 메서드==//
  /**
   * User의 학교를 변경합니다.
   * 양방향 관계를 일관성 있게 관리합니다.
   *
   * @param newSchool 변경할 학교 (null이면 학교에서 제거)
   */
  public void changeSchool(School newSchool) {
    // 기존 학교에서 제거
    if (this.school != null) {
      this.school.getUserList().remove(this);
    }

    // 새 학교에 추가
    this.school = newSchool;
    if (newSchool != null && !newSchool.getUserList().contains(this)) {
      newSchool.getUserList().add(this);
    }
  }

  /**
   * User를 학교에서 제거합니다.
   */
  public void removeFromSchool() {
    changeSchool(null);
  }

  //==방문 횟수 관리 헬퍼 메서드==//
  /**
   * 특정 mapType의 방문 횟수를 조회합니다.
   * 없으면 0을 반환합니다.
   */
  public Integer getVisitCount(String mapType) {
    return visitCounts.getOrDefault(mapType, 0);
  }

  /**
   * 특정 mapType의 방문 횟수를 증가시킵니다.
   */
  public void incrementVisitCount(String mapType) {
    visitCounts.put(mapType, getVisitCount(mapType) + 1);
  }

  /**
   * 특정 mapType의 방문 횟수를 설정합니다.
   */
  public void setVisitCount(String mapType, Integer count) {
    if (count == null || count < 0) {
      visitCounts.remove(mapType);
    } else {
      visitCounts.put(mapType, count);
    }
  }

  /**
   * 모든 방문 횟수를 조회합니다.
   */
  public Map<String, Integer> getAllVisitCounts() {
    return new HashMap<>(visitCounts);
  }

  /**
   * 모든 방문 횟수를 초기화합니다.
   */
  public void clearVisitCounts() {
    visitCounts.clear();
  }

}
