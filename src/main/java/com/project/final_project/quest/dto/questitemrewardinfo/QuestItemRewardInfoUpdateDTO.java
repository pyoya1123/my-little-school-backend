package com.project.final_project.quest.dto.questitemrewardinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestItemRewardInfoUpdateDTO {
  private Integer questItemRewardInfoId;
  private Integer questId;
  private Integer itemIdx;
  private Integer count;
}
