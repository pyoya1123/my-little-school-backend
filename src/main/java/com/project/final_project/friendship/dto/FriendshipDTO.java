package com.project.final_project.friendship.dto;

import com.project.final_project.friendship.domain.Friendship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDTO {

  private Integer id;
  private Integer requesterId;
  private Integer receiverId;
  private String message;
  private boolean isAccepted;

  public FriendshipDTO(Friendship friendship){
    this.id = friendship.getId();
    this.receiverId = friendship.getReceiverId();
    this.requesterId = friendship.getRequesterId();
    this.message = friendship.getMessage();
    this.isAccepted = friendship.isAccepted();
  }
}
