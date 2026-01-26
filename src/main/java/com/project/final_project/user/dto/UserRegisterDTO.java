package com.project.final_project.user.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRegisterDTO {

  String name;
  Integer grade;
  String birthday;
  Boolean gender;
  String email;
  String password;
  String phone;
  String statusMessage;
  Integer gold;
  List<String> interest = new ArrayList<>();
  Integer schoolId;

}
