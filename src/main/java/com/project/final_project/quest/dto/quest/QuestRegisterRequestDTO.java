package com.project.final_project.quest.dto.quest;

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
  private List<QuestRewardRequestDTO> rewardInfo;

}
