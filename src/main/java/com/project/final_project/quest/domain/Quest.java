package com.project.final_project.quest.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quest")
@Getter
@Setter
@NoArgsConstructor
public class Quest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "quest_title")
  private String title;

  @Column(name = "quest_content")
  private String content;

  @Column(name = "quest_count")
  private Integer count;

  @Column(name = "quest_type")
  private String questType;

  @Column(name = "gold")
  private Integer gold;

  @Column(name = "exp")
  private Integer exp;

  @OneToMany(mappedBy = "questId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<QuestItemRewardInfo> itemRewards = new ArrayList<>();


  public Quest(String title, String content, Integer count, String questType, Integer gold,
      Integer exp) {
    this.title = title;
    this.content = content;
    this.count = count;
    this.questType = questType;
    this.gold = gold;
    this.exp = exp;
  }

  public Quest(String title, String content, Integer count, String questType, Integer gold,
      Integer exp, List<QuestItemRewardInfo> itemRewards) {
    this.title = title;
    this.content = content;
    this.count = count;
    this.questType = questType;
    this.gold = gold;
    this.exp = exp;
    this.itemRewards = itemRewards;
  }
}
