package com.project.final_project.common.init;

import com.project.final_project.board.dto.BoardDTO;
import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.service.BoardService;
import com.project.final_project.comment.dto.CommentRequestDTO;
import com.project.final_project.comment.service.CommentService;
import com.project.final_project.common.util.MockDataGenerator;
import com.project.final_project.school.dto.SchoolRegisterDTO;
import com.project.final_project.school.repository.SchoolRepository;
import com.project.final_project.school.service.SchoolService;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.dto.UserRegisterDTO;
import com.project.final_project.user.service.UserService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Init {

  private final SchoolService schoolService;
  private final SchoolRepository schoolRepository;
  private final UserService userService;
  private final BoardService boardService;
  private final CommentService commentService;

  // 초기화 실행 여부 플래그 (한 번만 실행되도록)
  private static volatile boolean initialized = false;

  /**
   * 애플리케이션 시작 시 초기화 작업 수행
   */
  @PostConstruct
  public void init() {
    if (initialized) {
      log.debug("초기화 작업이 이미 완료되었습니다. 건너뜁니다.");
      return;
    }

    log.info("=== 애플리케이션 초기화 시작 ===");
    
    // 1. 학교 데이터 초기화
    // initSchools();
    
    // 2. 테스트 데이터 생성 (유저, 게시글, 댓글)
    // initTestData();

    // 초기화 완료 플래그 설정
    initialized = true;
    log.info("=== 애플리케이션 초기화 완료 ===");
  }
  
  private void initSchools() {
    if (!schoolService.existSchoolDatas()) {
      File excelFile = new File("src/main/resources/schools.xlsx");

      if (!excelFile.exists()) {
        log.warn("엑셀 파일이 존재하지 않습니다: {}", excelFile.getPath());
        return;
      }

      try (FileInputStream inputStream = new FileInputStream(excelFile);
          Workbook workbook = new XSSFWorkbook(inputStream)) {

        // 엑셀 데이터를 읽어서 DTO 리스트로 변환
        List<SchoolRegisterDTO> schoolData = extractSchoolData(workbook);
        for (SchoolRegisterDTO school : schoolData) {
          schoolService.registerSchool(school); // 학교 데이터 등록
        }

        log.info("학교 데이터가 성공적으로 저장되었습니다.");

      } catch (IOException e) {
        log.error("학교 데이터 초기화 중 오류 발생", e);
      }
    } else {
      log.info("학교 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
    }
  }

  private void initTestData() {
    long totalSchoolCount = schoolRepository.count();
    if (totalSchoolCount == 0) {
      log.warn("학교 데이터가 없어 테스트 데이터 생성을 건너뜁니다.");
      return;
    }

    log.info("=== 테스트 데이터 검증 및 보정 시작 ===");
    
    int targetUserCount = 2000;
    int targetBoardsPerUser = 5;
    int targetCommentsPerBoard = 3;
    
    // 1. 유저 수 확인 및 보충
    List<UserDTO> allUsers = userService.getAllUser();
    int currentUserCount = allUsers.size();
    
    if (currentUserCount < targetUserCount) {
      int usersToCreate = targetUserCount - currentUserCount;
      log.info("유저 부족 (현재: {}, 목표: {}). {}명 추가 생성 중...", currentUserCount, targetUserCount, usersToCreate);
      
      for (int i = 0; i < usersToCreate; i++) {
        try {
          Integer schoolId = MockDataGenerator.generateRandomSchoolId((int) totalSchoolCount);
          MockDataGenerator.UserRegisterData mockData = MockDataGenerator.generateUserRegisterData(schoolId);
          
          UserRegisterDTO dto = new UserRegisterDTO(
              mockData.name, mockData.grade, mockData.birthday, mockData.gender,
              mockData.email, mockData.password, mockData.phone, mockData.statusMessage,
              null, mockData.interest, mockData.schoolId
          );
          
          UserDTO createdUser = userService.registerUser(dto);
          allUsers.add(createdUser); // 리스트에도 추가
          
          if ((i + 1) % 500 == 0) {
            log.info("추가 유저 {}명 생성 완료", i + 1);
          }
        } catch (Exception e) {
          log.error("유저 생성 중 오류 발생: {}", e.getMessage());
        }
      }
    } else {
      log.info("유저 수 충분함 (현재: {}). 유저 생성 건너뜀.", currentUserCount);
    }
    
    // 2. 각 유저별 게시글 및 댓글 확인
    log.info("각 유저의 게시글 및 댓글 수 확인 중...");
    int processedUsers = 0;
    
    for (UserDTO user : allUsers) {
      try {
        // 해당 유저의 게시글 목록 조회 (개수 확인용)
        // 주의: getBoardListByUserId가 무거운 쿼리라면 성능 이슈 가능성 있음
        var boards = boardService.getBoardListByUserId(user.getId());
        int currentBoardCount = boards.size();
        
        // 게시글 부족하면 추가
        if (currentBoardCount < targetBoardsPerUser) {
          int boardsToCreate = targetBoardsPerUser - currentBoardCount;
          for (int j = 0; j < boardsToCreate; j++) {
            MockDataGenerator.BoardRegisterData mockBoard = MockDataGenerator.generateBoardRegisterData(user.getId());
            BoardRegisterDTO boardDto = new BoardRegisterDTO(mockBoard.title, mockBoard.content, mockBoard.userId);
            BoardDTO createdBoard = boardService.registerBoard(boardDto);
            
            // 새로 만든 게시글은 댓글도 새로 생성
            createCommentsForBoard(createdBoard.getBoardId(), targetCommentsPerBoard);
          }
        }
        
        // 기존 게시글들의 댓글 수 확인 및 보충
        for (var board : boards) {
          // getBoardListByUserId 결과인 BoardListResponseDTO에 이미 commentCount가 있다면 사용
          // 여기서는 BoardListResponseDTO의 구조를 모르므로 commentService 사용
          Long commentCount = commentService.getCommentCountByBoardId(board.getBoardId());
          
          if (commentCount < targetCommentsPerBoard) {
            long commentsToCreate = targetCommentsPerBoard - commentCount;
            createCommentsForBoard(board.getBoardId(), (int) commentsToCreate);
          }
        }
        
        processedUsers++;
        if (processedUsers % 500 == 0) {
          log.info("유저 {}명 데이터 검증 완료", processedUsers);
        }
        
      } catch (Exception e) {
        log.error("유저 {} 데이터 처리 중 오류: {}", user.getId(), e.getMessage());
      }
    }
    
    log.info("=== 테스트 데이터 검증 및 보정 완료 ===");
  }
  
  private void createCommentsForBoard(Integer boardId, int count) {
    for (int k = 0; k < count; k++) {
      MockDataGenerator.CommentRegisterData mockComment = MockDataGenerator.generateCommentRegisterData(boardId);
      CommentRequestDTO commentDto = new CommentRequestDTO(mockComment.content, mockComment.boardId);
      commentService.insertComment(commentDto);
    }
  }

  // 엑셀 데이터를 읽어서 SchoolRegisterDTO 리스트로 변환
  private List<SchoolRegisterDTO> extractSchoolData(Workbook workbook) {
    List<SchoolRegisterDTO> schools = new ArrayList<>();
    Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트

    for (Row row : sheet) {
      if (row.getRowNum() == 0) continue; // 헤더 행 건너뛰기

      // 학교명, 소재지지번주소, 위도, 경도 컬럼 읽기
      String schoolName = row.getCell(1).getStringCellValue();
      String address = row.getCell(7).getStringCellValue();
      double latitude = row.getCell(15).getNumericCellValue(); // 위도
      double longitude = row.getCell(16).getNumericCellValue(); // 경도

      // SchoolRegisterDTO 생성 및 리스트에 추가
      SchoolRegisterDTO school = new SchoolRegisterDTO(schoolName, address, latitude, longitude);
      schools.add(school);
    }

    return schools;
  }
}
