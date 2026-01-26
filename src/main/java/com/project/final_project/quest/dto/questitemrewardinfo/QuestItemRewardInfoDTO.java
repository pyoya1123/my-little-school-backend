package com.project.final_project.quest.dto.questitemrewardinfo;

import com.project.final_project.quest.domain.QuestItemRewardInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestItemRewardInfoDTO {

  private Integer questItemRewardInfoId;
  private Integer questId;
  private Integer itemIdx;
  private Integer count;

  public QuestItemRewardInfoDTO(QuestItemRewardInfo questItemRewardInfo) {
    this.questItemRewardInfoId = questItemRewardInfo.getId();
    this.questId = questItemRewardInfo.getQuestId();
    this.itemIdx = questItemRewardInfo.getItemIdx();
    this.count = questItemRewardInfo.getItemCount();
  }
}
