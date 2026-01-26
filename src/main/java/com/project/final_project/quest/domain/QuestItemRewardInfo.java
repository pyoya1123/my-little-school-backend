package com.project.final_project.quest.domain;

import com.project.final_project.quest.dto.questitemrewardinfo.QuestItemRewardInfoDTO;
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
@Table(name = "quest_item_reward_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestItemRewardInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "quest_id")
  private Integer questId;

  @Column(name = "item_idx")
  private Integer itemIdx;

  @Column(name = "item_count")
  private Integer itemCount;

  public QuestItemRewardInfo(Integer questId, Integer itemIdx, Integer itemCount) {
    this.questId = questId;
    this.itemIdx = itemIdx;
    this.itemCount = itemCount;
  }
}

