package com.project.final_project.guestbook.dto;

import com.project.final_project.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookRegisterDTO {

  private String content; // 쪽지 내용
  private User user; // 쪽지 남긴 유저
  private Integer mapId;
  private String mapType;
  private Integer backgroundColor;

}
