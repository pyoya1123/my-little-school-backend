package com.project.final_project.quest.dto.questReward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestRewardRegisterDTO {

  private Integer questId;
  private Integer itemIdx;
  private Integer count;

}
