package com.project.final_project.user.dto;

import com.project.final_project.school.dto.SchoolResponseDTO;
import com.project.final_project.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  Integer id;
  String name;
  Integer grade;
  String birthday;
  Boolean gender;
  String email;
  String password;
  String phone;
  String statusMesasge;
  Integer gold;
  List<String> interest = new ArrayList<>();
  Boolean isOnline;
  Integer mapId;
  String mapType;

  SchoolResponseDTO school;

  public UserDTO(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.grade = user.getGrade();
    this.birthday = user.getBirthday();
    this.gender = user.getGender();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.phone = user.getPhone();
    this.interest = user.getInterest() != null ? new ArrayList<>(user.getInterest()) : new ArrayList<>();
    this.statusMesasge = user.getStatusMessage();
    this.gold = user.getGold();
    this.isOnline = user.getIsOnline();
    this.school = new SchoolResponseDTO(user.getSchool());
    this.mapId = user.getMapId();
    this.mapType = user.getMapType();
  }
}
