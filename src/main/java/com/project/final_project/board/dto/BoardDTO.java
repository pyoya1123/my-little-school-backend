package com.project.final_project.board.dto;

import com.project.final_project.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
  Integer boardId;
  String title;
  String content;
  Integer likeCount;
  Integer commentCount;

  public BoardDTO(Board board, Integer commentCount) {
    this.boardId = board.getId();
    this.title = board.getTitle();
    this.content = board.getContent();
    this.likeCount = board.getLikeCount();
    this.commentCount = commentCount;
  }
}
