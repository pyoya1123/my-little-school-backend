package com.project.final_project.common.controller;

import com.project.final_project.board.domain.Board;
import com.project.final_project.board.dto.BoardDTO;
import com.project.final_project.board.dto.BoardListResponseDTO;
import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.repository.BoardRepository;
import com.project.final_project.board.service.BoardService;
import com.project.final_project.comment.domain.Comment;
import com.project.final_project.comment.dto.CommentRequestDTO;
import com.project.final_project.comment.repository.CommentRepository;
import com.project.final_project.comment.service.CommentService;
import com.project.final_project.common.dto.BulkCommentRequest;
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
  private final CommentRepository commentRepository;
  private final SchoolRepository schoolRepository;

  /**
   * 테스트용 사용자 데이터 생성
   * 
   * @param count 생성할 사용자 수
   * @param schoolId 학교 ID (null이면 랜덤 학교 사용)
   * @return 생성된 사용자 ID 목록
   */
  @PostMapping("/users")
  public ResponseEntity<Map<String, Object>> generateUsers(
      @RequestParam(defaultValue = "10") Integer count,
      @RequestParam(required = false) Integer schoolId) {
    
    // 전체 학교 수 조회
    long totalSchoolCount = schoolRepository.count();
    if (totalSchoolCount == 0) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "학교 데이터가 없습니다. 먼저 학교를 생성해주세요."));
    }

    List<Integer> createdUserIds = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      try {
        // schoolId가 지정되지 않으면 랜덤 학교 사용
        Integer targetSchoolId = schoolId;
        if (targetSchoolId == null) {
          targetSchoolId = MockDataGenerator.generateRandomSchoolId((int) totalSchoolCount);
        }
        
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
    response.put("totalSchools", totalSchoolCount);
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
//      var boards = boardRepository.findAll();
      return ResponseEntity.badRequest()
            .body(Map.of("error", "게시판이 없습니다. 먼저 게시판을 생성해주세요."));

      // 랜덤 게시판 선택
//      targetBoardId = boards.get((int) (Math.random() * boards.size())).getId();
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
   * 여러 게시글의 댓글 수 일괄 조회 (Bulk)
   * 대량의 게시글 댓글 수를 한 번에 조회합니다.
   * 
   * @param boardIds 조회할 게시글 ID 목록 (쉼표 구분)
   * @return boardId를 key, 댓글 수를 value로 하는 Map
   */
  @GetMapping("/comments/count-by-boards")
  public ResponseEntity<Map<String, Object>> getCommentCountsBulk(
      @RequestParam String boardIds) {
    
    if (boardIds == null || boardIds.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "boardIds 파라미터가 필요합니다."));
    }

    try {
      // 쉼표로 구분된 문자열을 Integer 리스트로 변환
      List<Integer> boardIdList = List.of(boardIds.split(","))
          .stream()
          .map(String::trim)
          .map(Integer::parseInt)
          .collect(Collectors.toList());

      if (boardIdList.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "유효한 boardId가 없습니다."));
      }

      // DB에서 GROUP BY로 한 번에 조회
      List<Object[]> results = commentRepository.getCommentCountsByBoardIds(boardIdList);
      
      // boardId -> commentCount 매핑
      Map<Integer, Long> commentCounts = new HashMap<>();
      for (Object[] row : results) {
        Integer boardId = (Integer) row[0];
        Long count = (Long) row[1];
        commentCounts.put(boardId, count);
      }

      // 요청한 모든 boardId에 대해 결과 생성 (댓글 없으면 0)
      Map<Integer, Long> response = new HashMap<>();
      for (Integer boardId : boardIdList) {
        response.put(boardId, commentCounts.getOrDefault(boardId, 0L));
      }

      Map<String, Object> result = new HashMap<>();
      result.put("requestedCount", boardIdList.size());
      result.put("commentCounts", response);

      return ResponseEntity.ok(result);
      
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "잘못된 boardId 형식입니다: " + e.getMessage()));
    }
  }

  /**
   * 여러 게시글의 댓글 수 일괄 조회 (Bulk - POST 버전)
   * 대량의 게시글 댓글 수를 한 번에 조회합니다. (URL 길이 제한 없음)
   * 
   * @param request boardIds 리스트
   * @return boardId를 key, 댓글 수를 value로 하는 Map
   */
  @PostMapping("/comments/count-by-boards")
  public ResponseEntity<Map<String, Object>> getCommentCountsBulkPost(
      @RequestBody Map<String, List<Integer>> request) {
    
    List<Integer> boardIdList = request.get("boardIds");
    
    if (boardIdList == null || boardIdList.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "boardIds 리스트가 필요합니다."));
    }

    try {
      // DB에서 GROUP BY로 한 번에 조회
      List<Object[]> results = commentRepository.getCommentCountsByBoardIds(boardIdList);
      
      // boardId -> commentCount 매핑
      Map<Integer, Long> commentCounts = new HashMap<>();
      for (Object[] row : results) {
        Integer boardId = (Integer) row[0];
        Long count = (Long) row[1];
        commentCounts.put(boardId, count);
      }

      // 요청한 모든 boardId에 대해 결과 생성 (댓글 없으면 0)
      Map<Integer, Long> response = new HashMap<>();
      for (Integer boardId : boardIdList) {
        response.put(boardId, commentCounts.getOrDefault(boardId, 0L));
      }

      Map<String, Object> result = new HashMap<>();
      result.put("requestedCount", boardIdList.size());
      result.put("commentCounts", response);

      return ResponseEntity.ok(result);
      
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "처리 중 오류 발생: " + e.getMessage()));
    }
  }

  /**
   * 게시글별 댓글 일괄 생성 (Bulk)
   * 대량의 게시글에 댓글을 한 번에 생성합니다.
   * 
   * @param request 게시글별 생성할 댓글 수 목록
   * @return 생성된 총 댓글 수 및 처리 결과
   */
  @PostMapping("/comments/bulk-by-boards")
  public ResponseEntity<Map<String, Object>> generateCommentsBulk(
      @RequestBody BulkCommentRequest request) {
    
    if (request.getItems() == null || request.getItems().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "items 목록이 비어있습니다."));
    }

    List<Comment> commentsToSave = new ArrayList<>();
    int totalRequestedCount = 0;
    int processedBoards = 0;
    List<String> errors = new ArrayList<>();

    // 각 게시글별로 댓글 생성
    for (BulkCommentRequest.BoardCommentItem item : request.getItems()) {
      try {
        Integer boardId = item.getBoardId();
        Integer count = item.getCount();
        
        if (boardId == null || count == null || count <= 0) {
          errors.add("Invalid item: boardId=" + boardId + ", count=" + count);
          continue;
        }

        totalRequestedCount += count;

        // count 개수만큼 댓글 생성
        for (int i = 0; i < count; i++) {
          MockDataGenerator.CommentRegisterData mockData = 
              MockDataGenerator.generateCommentRegisterData(boardId);
          
          Comment comment = new Comment(mockData.content, mockData.boardId);
          commentsToSave.add(comment);
        }
        
        processedBoards++;
        
      } catch (Exception e) {
        errors.add("BoardId " + item.getBoardId() + ": " + e.getMessage());
      }
    }

    // 배치 인서트 (JPA saveAll 사용)
    List<Comment> savedComments = commentRepository.saveAll(commentsToSave);

    Map<String, Object> response = new HashMap<>();
    response.put("requestedBoards", request.getItems().size());
    response.put("processedBoards", processedBoards);
    response.put("requestedComments", totalRequestedCount);
    response.put("createdComments", savedComments.size());
    
    if (!errors.isEmpty()) {
      response.put("errors", errors);
      response.put("errorCount", errors.size());
    }

    return ResponseEntity.ok(response);
  }

  /**
   * 모든 테스트 데이터 삭제
   */
  @DeleteMapping("/all")
  public ResponseEntity<Map<String, String>> deleteAllTestData() {
    return ResponseEntity.ok(Map.of("message", 
        "테스트 데이터 삭제는 개별적으로 수행해주세요. (안전을 위해)"));
  }
  
  /**
   * 기존 유저들의 학교를 랜덤하게 재할당
   * 
   * @return 업데이트된 유저 수
   */
  @PatchMapping("/users/randomize-schools")
  public ResponseEntity<Map<String, Object>> randomizeUserSchools() {
    try {
      // 전체 학교 수 조회
      long totalSchoolCount = schoolRepository.count();
      if (totalSchoolCount == 0) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "학교 데이터가 없습니다."));
      }

      // 모든 유저 조회
      List<UserDTO> users = userService.getAllUser();
      
      int updatedCount = 0;
      List<String> errors = new ArrayList<>();
      
      for (UserDTO userDTO : users) {
        try {
          // 랜덤 학교 ID 생성
          Integer randomSchoolId = MockDataGenerator.generateRandomSchoolId((int) totalSchoolCount);
          
          // 학교 정보 업데이트
          com.project.final_project.user.dto.UserUpdateDTO updateDTO = 
              new com.project.final_project.user.dto.UserUpdateDTO();
          updateDTO.setId(userDTO.getId());
          updateDTO.setSchoolId(randomSchoolId);
          
          userService.updateUser(updateDTO);
          updatedCount++;
        } catch (Exception e) {
          errors.add("User " + userDTO.getId() + ": " + e.getMessage());
        }
      }
      
      Map<String, Object> response = new HashMap<>();
      response.put("totalUsers", users.size());
      response.put("updatedCount", updatedCount);
      response.put("totalSchools", totalSchoolCount);
      response.put("message", "유저들의 학교 정보를 랜덤하게 업데이트했습니다.");
      
      if (!errors.isEmpty()) {
        response.put("errors", errors);
        response.put("errorCount", errors.size());
      }
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "학교 랜덤 재할당 중 오류 발생: " + e.getMessage()));
    }
  }
}

