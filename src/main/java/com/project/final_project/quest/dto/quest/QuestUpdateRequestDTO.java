package com.project.final_project.quest.dto.quest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestUpdateRequestDTO {

  private Integer questId;
  private String title;
  private String content;
  private String questType;
  private Integer count;
  private Integer gold;
  private Integer exp;
}
