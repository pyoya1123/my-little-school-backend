package com.project.final_project.quest.domain;

import com.project.final_project.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user_quest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserQuest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "quest_id", nullable = false)
  private Quest quest;

  @Column(name = "quest_count")
  private Integer count;

  @Column(name = "is_complete", nullable = false)
  private Boolean isComplete = false;

  public UserQuest(User user, Quest quest, Integer count, Boolean isComplete) {
    this.user = user;
    this.quest = quest;
    this.count = count;
    this.isComplete = isComplete;
  }
}
