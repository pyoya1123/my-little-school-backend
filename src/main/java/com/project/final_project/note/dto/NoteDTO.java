package com.project.final_project.note.dto;

import com.project.final_project.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoteDTO {

  private Integer toUserId;
  private Integer fromUserId;
  private Integer color;
  private String content;

  public NoteDTO(Note note){
    this.toUserId = note.getToUserId();
    this.fromUserId = note.getFromUserId();
    this.color = note.getColor();
    this.content = note.getContent();
  }
}
