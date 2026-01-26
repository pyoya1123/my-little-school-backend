package com.project.final_project.user.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.guestbook.domain.GuestBook;
import com.project.final_project.quest.domain.UserQuest;
import com.project.final_project.school.domain.School;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

  @ElementCollection(fetch = FetchType.EAGER)
  @Column(name = "interest")
  List<String> interest = new ArrayList<>();

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
    return 100 + (level - 1) * 50;
  }


}
