package com.project.final_project.user.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
  Integer id;
  String name;
  Integer grade;
  String birthday;
  Boolean gender;
  String email;
  String password;
  String phone;
  List<String> interest;
  String statusMesasge;
  Integer gold;
  Integer schoolId;
}
