package com.project.final_project.comment.dto;

import com.project.final_project.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

  private Integer commentId;
  private String content;
  private Integer boardId;

  public CommentResponseDTO(Comment comment) {
    this.commentId = comment.getId();
    this.content = comment.getContent();
    this.boardId = comment.getBoardId();
  }
}

