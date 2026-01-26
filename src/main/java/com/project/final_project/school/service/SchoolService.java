package com.project.final_project.school.service;

import static java.util.stream.Collectors.toList;

import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.furniture.dto.FurnitureRegisterDTO;
import com.project.final_project.furniture.repository.FurnitureRepository;
import com.project.final_project.furniture.service.FurnitureService;
import com.project.final_project.schedule.service.ScheduleService;
import com.project.final_project.school.domain.School;
import com.project.final_project.school.dto.SchoolDTO;
import com.project.final_project.school.dto.SchoolRegisterDTO;
import com.project.final_project.school.dto.SchoolResponseDTO;
import com.project.final_project.school.repository.SchoolRepository;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.repository.UserRepository;
import com.project.final_project.user.service.UserService;
import com.project.final_project.websocket.service.UserStatusService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolService {

  private final SchoolRepository schoolRepository;
  private final UserRepository userRepository;
  private final UserStatusService userStatusService;

  public SchoolDTO registerSchool(SchoolRegisterDTO schoolRegisterDTO) {
    School school = new School(
        schoolRegisterDTO.getSchoolName(),
        schoolRegisterDTO.getLocation(),
        schoolRegisterDTO.getLongitude(),
        schoolRegisterDTO.getLatitude()
    );

    School savedSchool = schoolRepository.save(school);

    return new SchoolDTO(savedSchool);
  }

  @Transactional
  public SchoolDTO addUserToSchool(Integer schoolId, Integer userId, Integer gradeId) {
    School foundSchool = schoolRepository.findById(schoolId).orElseThrow(
        () -> new IllegalArgumentException("not found school id : " + schoolId));

    User foundUser = userRepository.findById(userId).orElseThrow(
        () -> new IllegalArgumentException("not found user id : " + userId));

    foundSchool.getUserList().add(foundUser);
    foundUser.setSchool(foundSchool);
    foundUser.setGrade(gradeId);

    return new SchoolDTO(foundSchool);
  }

  public List<User> getUserListBySchoolId(Integer schoolId) {
    School foundSchool = schoolRepository.findById(schoolId).orElseThrow(
        () -> new IllegalArgumentException("not found school id : " + schoolId));

    return foundSchool.getUserList();
  }

  public School getSchoolById(Integer schoolId) {
    return schoolRepository.findById(schoolId).orElseThrow(
        () -> new IllegalArgumentException("not found school id : " + schoolId));
  }

  public List<SchoolDTO> getAllSchool() {
    return schoolRepository.findAll().stream()
        .map(SchoolDTO::new)
        .toList();
  }

  @Transactional
  public void deleteSchoolById(Integer schoolId) {
    schoolRepository.deleteById(schoolId);
  }

  @Transactional
  public void deleteUserInUserList(Integer schoolId, Integer userId) {
    School school = schoolRepository.findById(schoolId).orElseThrow(
        () -> new IllegalArgumentException("NOT FOUND School ID : " + schoolId)
    );

    // UserList에서 userId에 해당하는 유저 제거
    boolean removed = school.getUserList().removeIf(user -> user.getId().equals(userId));

    if (!removed) {
      throw new IllegalArgumentException("NOT FOUND User ID : " + userId + " in School ID : " + schoolId);
    }
  }


  public List<SchoolResponseDTO> getSchoolListBySchoolName(String schoolName) {
    return schoolRepository.findBySchoolNameContaining(schoolName)
        .stream()
        .map(s -> new SchoolResponseDTO(
            s.getId(),
            s.getSchoolName(),
            s.getLocation(),
            userStatusService.getUserCountInSchool(s.getId(), "School")
            )
        )
        .toList();
  }

  public List<School> getSchoolListWithinRadiusBySchoolName(String schoolName, double radius) {
    return schoolRepository.getSchoolListWithinRadiusBySchoolName(schoolName, radius);
  }

  public boolean existSchoolDatas() {
    return !schoolRepository.findAll().isEmpty();
  }
}
