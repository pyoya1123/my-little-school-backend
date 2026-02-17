package com.project.final_project.quest.dto.questReward;

import com.project.final_project.quest.domain.QuestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestRewardDTO {

  private Integer QuestRewardId;
  private Integer questId;
  private Integer itemIdx;
  private Integer count;

  public QuestRewardDTO(QuestReward questReward) {
    this.QuestRewardId = questReward.getId();
    this.questId = questReward.getQuest().getId();
    this.itemIdx = questReward.getItem().getId();
    this.count = questReward.getItemCount();
  }
}
