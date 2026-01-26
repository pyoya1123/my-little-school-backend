package com.project.final_project.quest.dto.quest;

import com.project.final_project.quest.domain.QuestItemRewardInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestRegisterRequestDTO {

  private String title;
  private String content;
  private String questType;
  private Integer count;
  private Integer gold;
  private Integer exp;
  private List<QuestItemRewardInfo> rewardInfo;

}
