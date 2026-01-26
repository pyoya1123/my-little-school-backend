package com.project.final_project.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateDTO {
  Integer boardId;
  String title;
  String content;
  Integer likeCount;
}
