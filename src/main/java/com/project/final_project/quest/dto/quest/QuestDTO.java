package com.project.final_project.quest.dto.quest;

import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.dto.questReward.QuestRewardDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestDTO {

  private Integer questId;
  private String title;
  private String content;
  private String questType;
  private Integer count;
  private Integer gold;
  private Integer exp;
  private List<QuestRewardDTO> reward;

  public QuestDTO(Quest quest) {
    this.questId = quest.getId();
    this.title = quest.getTitle();
    this.content = quest.getContent();
    this.questType = quest.getQuestType();
    this.count = quest.getCount();
    this.gold = quest.getGold();
    this.exp = quest.getExp();
    this.reward = quest.getRewards().stream()
        .map(QuestRewardDTO::new)
        .collect(Collectors.toList());
  }

}
