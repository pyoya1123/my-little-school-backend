package com.project.final_project.school.dto;

import com.project.final_project.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SchoolRegisterDTO {
  String schoolName;
  String location;
  Double longitude;
  Double latitude;
  List<User> userList = new ArrayList<>();

  public SchoolRegisterDTO(String schoolName, String location, Double longitude, Double latitude) {
    this.schoolName = schoolName;
    this.location = location;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
