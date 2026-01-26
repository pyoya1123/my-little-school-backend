package com.project.final_project.school.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.school.domain.School;
import com.project.final_project.school.dto.SchoolDTO;
import com.project.final_project.school.dto.SchoolRegisterDTO;
import com.project.final_project.school.dto.SchoolResponseDTO;
import com.project.final_project.school.service.SchoolService;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

  private final SchoolService schoolService;
  private final UserService userService;

  @GetMapping("/list")
  public ResponseResult<List<SchoolDTO>> getAllSchool() {
    return success(schoolService.getAllSchool());
  }

  @GetMapping
  public ResponseEntity<SchoolDTO> getSchoolById(@RequestParam("schoolId") Integer schoolId) {
    School school = schoolService.getSchoolById(schoolId);
    return ResponseEntity.ok(
        new SchoolDTO(school));
  }

  @GetMapping("/user-list")
  public ResponseEntity<List<UserDTO>> getUserListBySchoolId(@RequestParam("schoolId") Integer schoolId) {
    List<User> userList = schoolService.getUserListBySchoolId(schoolId);
    School school = schoolService.getSchoolById(schoolId);
    return ResponseEntity.ok(userList.stream()
        .map(UserDTO::new)
        .collect(Collectors.toList()));
  }

  @GetMapping("/{schoolName}")
  public List<SchoolResponseDTO> getSchoolListBySchoolName(
      @PathVariable("schoolName") String schoolName) {
    return schoolService.getSchoolListBySchoolName(schoolName);
  }

  @GetMapping("/nearby/{schoolName}/{radius}")
  public List<SchoolDTO> getSchoolListWithinRadiusBySchoolName(
      @PathVariable("schoolName") String schoolName,
      @PathVariable("radius") double radius) {
    List<School> schools = schoolService.getSchoolListWithinRadiusBySchoolName(schoolName, radius);
    return schools.stream().map(SchoolDTO::new).toList();
  }

  @PostMapping
  public ResponseEntity<SchoolDTO> registerSchool(@RequestBody SchoolRegisterDTO schoolRegisterDTO) {
    return ResponseEntity.ok(schoolService.registerSchool(schoolRegisterDTO));
  }

  @PostMapping("/add-user")
  public ResponseEntity<SchoolDTO> addUserToSchool(@RequestParam("schoolId") Integer schoolId,
      @RequestParam("userId") Integer userId, @RequestParam("user_grade") Integer gradeId) {
    return ResponseEntity.ok(schoolService.addUserToSchool(schoolId, userId, gradeId));
  }

  @DeleteMapping
  public ResponseEntity<?> deleteSchoolById(@RequestParam("schoolId") Integer schoolId) {
    schoolService.deleteSchoolById(schoolId);
    return ResponseEntity.ok().build();
  }
}
