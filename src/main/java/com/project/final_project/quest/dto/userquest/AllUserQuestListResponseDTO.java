package com.project.final_project.quest.dto.userquest;

import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.UserQuest;
import com.project.final_project.quest.dto.quest.QuestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AllUserQuestListResponseDTO {

  Integer userQuestId;
  QuestDTO quest;
  Integer count;
  Boolean isComplete = false;

  public AllUserQuestListResponseDTO(UserQuest q){
    this.userQuestId = q.getId();
    this.quest = new QuestDTO(q.getQuest());
    this.count = q.getCount();
    this.isComplete = q.getIsComplete();
  }

}
