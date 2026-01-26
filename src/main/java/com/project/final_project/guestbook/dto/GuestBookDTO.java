package com.project.final_project.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.guestbook.domain.GuestBook;
import com.project.final_project.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookDTO {

  private String content; // 쪽지 내용
  private User user; // 쪽지 남긴 유저
  private Integer id;
  private Integer mapId;
  private String mapType;
  private Integer backgroundColor;

  public GuestBookDTO(GuestBook gb) {
    this.id = gb.getId();
    this.content = gb.getContent();
    this.user = gb.getUser();
    this.mapId = gb.getMapId();
    this.mapType = gb.getMapType();
    this.backgroundColor = gb.getBackgroundColor();
  }
}
