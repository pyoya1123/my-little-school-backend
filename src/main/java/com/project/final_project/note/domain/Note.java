package com.project.final_project.note.domain;

import com.project.final_project.note.dto.NoteRegisterDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "note")
@Getter
@Setter
public class Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "to_user_id")
  private Integer toUserId;

  @Column(name = "from_user_id")
  private Integer fromUserId;

  @Column(name = "color")
  private Integer color;

  @Column(name = "content")
  private String content;

  public static Note createNote(NoteRegisterDTO dto) {
    Note newNote = new Note();
    newNote.setColor(dto.getColor());
    newNote.setContent(dto.getContent());
    newNote.setToUserId(dto.getToUserId());
    newNote.setFromUserId(dto.getFromUserId());
    return newNote;
  }

}
