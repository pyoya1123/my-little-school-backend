package com.project.final_project.quest.domain;

import com.project.final_project.item.domain.Item;
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
@Table(name = "quest_reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestReward {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quest_id")
  private Quest quest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @Column(name = "item_count")
  private Integer itemCount;

  public QuestReward(Quest quest, Item item, Integer itemCount) {
    this.quest = quest;
    this.item = item;
    this.itemCount = itemCount;
  }
}
