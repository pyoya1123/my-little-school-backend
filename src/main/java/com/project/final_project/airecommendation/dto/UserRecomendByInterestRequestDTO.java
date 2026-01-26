package com.project.final_project.airecommendation.dto;

import com.project.final_project.user.domain.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRecomendByInterestRequestDTO {

  Integer userId;
  List<String> interest;

  public UserRecomendByInterestRequestDTO(User user) {
    this.userId = user.getId();
    this.interest = user.getInterest();
  }

}
