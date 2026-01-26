package com.project.final_project.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.school.domain.School;
import com.project.final_project.user.dto.UserDTO;
import java.util.List;
import lombok.Getter;

@Getter
public class SchoolDTO {

  Integer id;
  String schoolName;
  String location;
  List<UserDTO> userList;

  public SchoolDTO(School school) {
    this.id  = school != null ? school.getId() : null;
    this.schoolName = school != null ? school.getSchoolName() : null;
    this.location = school != null ? school.getLocation() : null;
    this.userList = school != null && school.getUserList() != null
        ? school.getUserList().stream().map(UserDTO::new).toList()
        : null;
  }
}
