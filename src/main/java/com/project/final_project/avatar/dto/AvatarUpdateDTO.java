package com.project.final_project.avatar.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvatarUpdateDTO {
  Integer userId;
  List<Integer> infoList;
}
