package com.project.final_project.user.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.schedule.service.ScheduleService;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.dto.UserPosDTO;
import com.project.final_project.user.dto.UserPosUpdateDTO;
import com.project.final_project.user.dto.UserProfileDTO;
import com.project.final_project.user.dto.UserRegisterDTO;
import com.project.final_project.user.dto.UserRegisterSchoolDTO;
import com.project.final_project.user.dto.UserRequestOnlineStatusDTO;
import com.project.final_project.user.dto.UserUpdateDTO;
import com.project.final_project.user.service.UserService;
import java.util.List;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final ScheduleService scheduleService;

  @GetMapping("/{userId}")
  public UserDTO getUserById(@PathVariable("userId") Integer userId) {
    User user = userService.getUser(userId);
    return new UserDTO(user);
  }

  @GetMapping("/email/{userEmail}")
  public UserDTO getUserByEmail(@PathVariable("userEmail") String userEmail){
    User foundUser = userService.getUserByEmail(userEmail);
    return new UserDTO(foundUser);
  }

  /**
   * [기존 방식] 모든 사용자 목록 조회
   * 단순 findAll() 사용, N+1 문제 가능성 있음
   */
  @GetMapping("/list")
  public ResponseResult<List<UserDTO>> getAllUsers() {
    List<UserDTO> allUser = userService.getAllUser();
    return success(allUser);
  }

  /**
   * [최적화 버전] 모든 사용자 목록 조회
   * JOIN FETCH 사용 + Observation API 메트릭 측정
   * 비교 분석용 엔드포인트
   */
  @GetMapping("/list/optimized")
  public ResponseResult<List<UserDTO>> getAllUsersOptimized() {
    List<UserDTO> allUser = userService.getAllUserOptimized();
    return success(allUser);
  }

  @GetMapping("/is-exist/{userEmail}")
  public Boolean isUserExistByUserEmail(@PathVariable("userEmail") String userEmail) {
    return userService.isUserExistByUserEmail(userEmail);
  }

  @PostMapping
  public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegisterDTO dto){
    return ResponseEntity.ok(userService.registerUser(dto));
  }

  @PatchMapping
  public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateDTO dto){
    return ResponseEntity.ok(userService.updateUser(dto));
  }

  @PatchMapping("/register-school")
  public ResponseResult<UserDTO> registerSchoolToUser(@RequestBody UserRegisterSchoolDTO dto){
    return success(userService.registerSchoolToUser(dto));
  }

  @DeleteMapping
  public ResponseEntity<?> deleteUser(@RequestParam("userId") Integer id){
    userService.removeUser(id);
    return ResponseEntity.noContent().build();
  }



  //== 프로필 ==//
  @GetMapping("/profile/{userId}")
  public ResponseResult<UserProfileDTO> getProfile(@PathVariable("userId") Integer userId) {
    return success(userService.getProfile(userId));
  }

  @PatchMapping("/profile")
  public ResponseResult<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO dto) {
    return success(new UserProfileDTO(userService.updateProfile(dto)));
  }



  //== 유저 맵 이동 시 위치 정보 메소드 ==//
  @GetMapping("/pos/{userId}")
  public UserPosDTO getPosition(@PathVariable("userId") Integer userId) {
    return userService.getPosition(userId);
  }

  @PatchMapping("/pos")
  public ResponseResult<UserPosDTO> updatePosition(@RequestBody UserPosUpdateDTO dto) {
    return userService.updatePosition(dto);
  }


}
