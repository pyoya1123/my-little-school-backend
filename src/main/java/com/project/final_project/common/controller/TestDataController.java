package com.project.final_project.common.controller;

import com.project.final_project.board.domain.Board;
import com.project.final_project.board.dto.BoardDTO;
import com.project.final_project.board.dto.BoardListResponseDTO;
import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.repository.BoardRepository;
import com.project.final_project.board.service.BoardService;
import com.project.final_project.comment.dto.CommentRequestDTO;
import com.project.final_project.comment.service.CommentService;
import com.project.final_project.common.util.MockDataGenerator;
import com.project.final_project.school.repository.SchoolRepository;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.dto.UserRegisterDTO;
import com.project.final_project.user.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  private final BoardRepository boardRepository;
  private final CommentService commentService;
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
   * 테스트용 댓글 데이터 생성
   * 
   * @param count 생성할 댓글 수
   * @param boardId 게시판 ID (null이면 랜덤 게시판 사용)
   * @return 생성된 댓글 ID 목록
   */
  @PostMapping("/comments")
  public ResponseEntity<Map<String, Object>> generateComments(
      @RequestParam(defaultValue = "10") Integer count,
      @RequestParam(required = false) Integer boardId) {
    
    List<Integer> createdCommentIds = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    // boardId가 없으면 기존 게시판 중 랜덤 선택
    Integer targetBoardId = boardId;
    if (targetBoardId == null) {
      var boards = boardRepository.findAll();
      if (boards.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "게시판이 없습니다. 먼저 게시판을 생성해주세요."));
      }
      // 랜덤 게시판 선택
      targetBoardId = boards.get((int) (Math.random() * boards.size())).getId();
    }

    for (int i = 0; i < count; i++) {
      try {
        MockDataGenerator.CommentRegisterData mockData = 
            MockDataGenerator.generateCommentRegisterData(targetBoardId);
        
        CommentRequestDTO dto = new CommentRequestDTO(
            mockData.content,
            mockData.boardId
        );

        var createdComment = commentService.insertComment(dto);
        createdCommentIds.add(createdComment.getCommentId());
      } catch (Exception e) {
        errors.add("Comment " + i + ": " + e.getMessage());
      }
    }

    Map<String, Object> response = new HashMap<>();
    response.put("createdCount", createdCommentIds.size());
    response.put("requestedCount", count);
    response.put("commentIds", createdCommentIds);
    response.put("boardId", targetBoardId);
    if (!errors.isEmpty()) {
      response.put("errors", errors);
    }

    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/list")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
      List<UserDTO> userList = userService.getAllUser();
      return ResponseEntity.ok(userList);
  }

  @GetMapping("/board/list")
  public ResponseEntity<List<Board>> getAllBoards() {
    return ResponseEntity.ok(boardRepository.findAll());
  }

  /**
   * 모든 테스트 데이터 삭제
   */
  @DeleteMapping("/all")
  public ResponseEntity<Map<String, String>> deleteAllTestData() {
    return ResponseEntity.ok(Map.of("message", 
        "테스트 데이터 삭제는 개별적으로 수행해주세요. (안전을 위해)"));
  }
}

