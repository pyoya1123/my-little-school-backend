package com.project.final_project.user.dto;

import com.project.final_project.user.domain.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

  Integer id;
  String name;
  List<String> interest;
  String statusMessage;

  public UserProfileDTO(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.interest = user.getInterest();
    this.statusMessage = user.getStatusMessage();
  }

  public UserProfileDTO(UserProfileDTO dto){
    this.id = dto.getId();
    this.name = dto.getName();
    this.interest = dto.getInterest();
    this.statusMessage = dto.getStatusMessage();
  }
}
