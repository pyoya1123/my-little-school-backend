package com.project.final_project.friendship.dto;

import com.project.final_project.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipResponseDTO {

  private Integer id;
  private UserDTO requester;
  private UserDTO receiver;
  private String message;
  private boolean isAccepted;

}
