package com.project.final_project.quest.dto.userquest;

import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.UserQuest;
import com.project.final_project.quest.dto.quest.QuestDTO;
import com.project.final_project.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestDTO {

  Integer userQuestId;
  QuestDTO quest;
  Integer count;
  Boolean isComplete = false;

  public UserQuestDTO(UserQuest q){
    this.userQuestId = q.getId();
    this.quest = new QuestDTO(q.getQuest());
    this.count = q.getCount();
    this.isComplete = q.getIsComplete();
  }

}
