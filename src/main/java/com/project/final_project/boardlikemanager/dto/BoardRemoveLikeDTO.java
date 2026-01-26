package com.project.final_project.boardlikemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardRemoveLikeDTO {

  Integer boardId;
  Integer userId;


  @Override
  public String toString() {
    return "BoardRemoveLIkeDTO{" +
        "boardId=" + boardId +
        ", userId=" + userId +
        '}';
  }
}
