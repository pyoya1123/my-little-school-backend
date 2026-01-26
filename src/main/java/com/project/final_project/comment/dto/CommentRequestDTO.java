package com.project.final_project.comment.dto;

import com.project.final_project.comment.domain.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {

  private String content;
  private Integer boardId;

  public CommentRequestDTO(Comment comment) {
    this.content = comment.getContent();
    this.boardId = comment.getBoardId();
  }

}
