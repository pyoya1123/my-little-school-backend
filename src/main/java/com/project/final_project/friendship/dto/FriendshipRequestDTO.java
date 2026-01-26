package com.project.final_project.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipRequestDTO {

  private Integer requesterId;
  private Integer receiverId;
  private String message;

}
