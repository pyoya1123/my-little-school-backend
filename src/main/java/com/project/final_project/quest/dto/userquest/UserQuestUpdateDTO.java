package com.project.final_project.quest.dto.userquest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestUpdateDTO {
  Integer userQuestId;
  Integer userId;
  Integer questId;
  Integer count;
  Boolean isComplete = false;

}
