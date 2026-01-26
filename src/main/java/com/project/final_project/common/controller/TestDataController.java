package com.project.final_project.common.controller;

import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.service.BoardService;
import com.project.final_project.common.util.MockDataGenerator;
import com.project.final_project.school.repository.SchoolRepository;
import com.project.final_project.user.dto.UserRegisterDTO;
import com.project.final_project.user.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트 데이터 생성 컨트롤러
 * 성능 테스트를 위한 목업 데이터를 생성합니다.
 */
@RestController
@RequestMapping("/test-data")
@RequiredArgsConstructor
public class TestDataController {

  private final UserService userService;
  private final BoardService boardService;
  private final SchoolRepository schoolRepository;

  /**
   * 테스트용 사용자 데이터 생성
   * 
   * @param count 생성할 사용자 수
   * @param schoolId 학교 ID (null이면 첫 번째 학교 사용)
   * @return 생성된 사용자 ID 목록
   */
  @PostMapping("/users")
  public ResponseEntity<Map<String, Object>> generateUsers(
      @RequestParam(defaultValue = "10") Integer count,
      @RequestParam(required = false) Integer schoolId) {
    
    // 학교 ID가 없으면 첫 번째 학교 사용
    Integer targetSchoolId = schoolId;
    if (targetSchoolId == null) {
      targetSchoolId = schoolRepository.findAll().stream()
          .findFirst()
          .map(school -> school.getId())
          .orElse(null);
    }

    List<Integer> createdUserIds = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      try {
        MockDataGenerator.UserRegisterData mockData = 
            MockDataGenerator.generateUserRegisterData(targetSchoolId);
        
        // UserRegisterDTO 생성자 순서: name, grade, birthday, gender, email, password, phone, statusMessage, gold, interest, schoolId
        UserRegisterDTO dto = new UserRegisterDTO(
            mockData.name,           // name
            mockData.grade,          // grade
            mockData.birthday,       // birthday
            mockData.gender,         // gender
            mockData.email,          // email
            mockData.password,       // password
            mockData.phone,          // phone
            mockData.statusMessage,  // statusMessage
            null,                    // gold (null로 설정, 서비스에서 기본값 설정)
            mockData.interest,       // interest
            mockData.schoolId        // schoolId
        );

        var createdUser = userService.registerUser(dto);
        createdUserIds.add(createdUser.getId());
      } catch (Exception e) {
        errors.add("User " + i + ": " + e.getMessage());
      }
    }

    Map<String, Object> response = new HashMap<>();
    response.put("createdCount", createdUserIds.size());
    response.put("requestedCount", count);
    response.put("userIds", createdUserIds);
    if (!errors.isEmpty()) {
      response.put("errors", errors);
    }

    return ResponseEntity.ok(response);
  }

  /**
   * 테스트용 게시판 데이터 생성
   * 
   * @param count 생성할 게시판 수
   * @param userId 사용자 ID (null이면 랜덤 사용자 사용)
   * @return 생성된 게시판 ID 목록
   */
  @PostMapping("/boards")
  public ResponseEntity<Map<String, Object>> generateBoards(
      @RequestParam(defaultValue = "10") Integer count,
      @RequestParam(required = false) Integer userId) {
    
    List<Integer> createdBoardIds = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    // userId가 없으면 기존 사용자 중 랜덤 선택
    Integer targetUserId = userId;
    if (targetUserId == null) {
      var users = userService.getAllUser();
      if (users.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "사용자가 없습니다. 먼저 사용자를 생성해주세요."));
      }
      targetUserId = users.get((int) (Math.random() * users.size())).getId();
    }

    for (int i = 0; i < count; i++) {
      try {
        MockDataGenerator.BoardRegisterData mockData = 
            MockDataGenerator.generateBoardRegisterData(targetUserId);
        
        BoardRegisterDTO dto = new BoardRegisterDTO(
            mockData.title,
            mockData.content,
            mockData.userId
        );

        var createdBoard = boardService.registerBoard(dto);
        createdBoardIds.add(createdBoard.getBoardId());
      } catch (Exception e) {
        errors.add("Board " + i + ": " + e.getMessage());
      }
    }

    Map<String, Object> response = new HashMap<>();
    response.put("createdCount", createdBoardIds.size());
    response.put("requestedCount", count);
    response.put("boardIds", createdBoardIds);
    response.put("userId", targetUserId);
    if (!errors.isEmpty()) {
      response.put("errors", errors);
    }

    return ResponseEntity.ok(response);
  }

  /**
   * 모든 테스트 데이터 삭제
   */
  @DeleteMapping("/all")
  public ResponseEntity<Map<String, String>> deleteAllTestData() {
    // 주의: 실제 운영 환경에서는 이 엔드포인트를 비활성화해야 합니다.
    // 여기서는 예시만 제공하고, 실제 구현은 신중하게 해야 합니다.
    return ResponseEntity.ok(Map.of("message", 
        "테스트 데이터 삭제는 개별적으로 수행해주세요. (안전을 위해)"));
  }
}

