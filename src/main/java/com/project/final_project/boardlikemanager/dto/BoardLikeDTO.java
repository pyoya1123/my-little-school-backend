package com.project.final_project.boardlikemanager.dto;

import com.project.final_project.boardlikemanager.domain.BoardLike;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardLikeDTO {

  Integer boardLikeId;
  Integer boardId;
  Integer userId;

  public BoardLikeDTO(BoardLike bl){
    this.boardLikeId = bl.getId();
    this.userId = bl.getUserId();
    this.boardId = bl.getBoardId();
  }

}
