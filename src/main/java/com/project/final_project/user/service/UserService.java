package com.project.final_project.user.service;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.airecommendation.dto.UserRecomendByInterestRequestDTO;
import com.project.final_project.airecommendation.service.AIRecommendationService;
import com.project.final_project.avatar.service.AvatarService;
import com.project.final_project.board.service.BoardService;
import com.project.final_project.boardlikemanager.service.BoardLikeService;
import com.project.final_project.chatbotlog.service.ChatBotLogService;
import com.project.final_project.chatlog.service.ChatLogService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.emotionanalysis.service.EmotionAnalysisService;
import com.project.final_project.friendship.service.FriendshipService;
import com.project.final_project.furniture.service.FurnitureService;
import com.project.final_project.gallery.service.GalleryService;
import com.project.final_project.guestbook.service.GuestBookService;
import com.project.final_project.inventory.service.InventoryService;
import com.project.final_project.mapcontest.service.MapContestService;
import com.project.final_project.note.service.NoteService;
import com.project.final_project.quest.dto.quest.QuestDTO;
import com.project.final_project.quest.dto.userquest.UserQuestRegisterRequestDTO;
import com.project.final_project.quest.service.QuestService;
import com.project.final_project.quest.service.UserQuestService;
import com.project.final_project.school.domain.School;
import com.project.final_project.school.repository.SchoolRepository;
import com.project.final_project.school.service.SchoolService;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.dto.UserPosDTO;
import com.project.final_project.user.dto.UserPosUpdateDTO;
import com.project.final_project.user.dto.UserProfileDTO;
import com.project.final_project.user.dto.UserRegisterDTO;
import com.project.final_project.user.dto.UserRegisterSchoolDTO;
import com.project.final_project.user.dto.UserUpdateDTO;
import com.project.final_project.user.repository.UserRepository;
import com.project.final_project.userposvisitcount.service.UserPosVisitCountService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final SchoolRepository schoolRepository;
  private final AIRecommendationService aiRecommendationService;
  private final QuestService questService;
  private final UserQuestService userQuestService;
  private final InventoryService inventoryService;
  private final AvatarService avatarService;
  private final BoardService boardService;
  private final ChatLogService chatLogService;
  private final ChatBotLogService chatBotLogService;
  private final FriendshipService friendshipService;
  private final FurnitureService furnitureService;
  private final GalleryService galleryService;
  private final GuestBookService guestBookService;
  private final MapContestService mapContestService;
  private final NoteService noteService;
  private final UserPosVisitCountService userPosVisitCountService;
  private final BoardLikeService boardLikeService;
  private final SchoolService schoolService;
  private final EmotionAnalysisService emotionAnalysisService;

  public User getUser(Integer id) {
    return userRepository.findById(id).orElse(null);
  }

  public List<UserDTO> getAllUser() {
    return userRepository.findAll().stream()
        .map(UserDTO::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public UserDTO registerUser(UserRegisterDTO dto) {
    try {
      User foundUser = userRepository.getUserByEmail(dto.getEmail());
      if(foundUser != null) {
        throw new IllegalArgumentException("해당 이메일은 이미 존재합니다.");
      }

      // 유저 생성 및 저장
      User newUser = createUser(dto);
      User savedUser = userRepository.save(newUser);

      // 관심사가 존재하면 AI 추천 서비스 호출
      if (savedUser.getInterest() != null && !savedUser.getInterest().isEmpty()) {
        try {
          aiRecommendationService.sendInterestToAI(new UserRecomendByInterestRequestDTO(savedUser));
        } catch (Exception e) {
          // AI 서비스 호출 실패 시 로그 남기기
          System.err.println("Failed to send user interest to AI service: " + e.getMessage());
        }
      }

      //== 유저 생성할 때 초기 퀘스트 등록 ==//
      // TUTORIAL 퀘스트 가져오기
      List<QuestDTO> tutorial;
      try {
        tutorial = questService.getQuestListByQuestType("TUTORIAL");
      } catch (Exception e) {
        throw new RuntimeException("Failed to fetch TUTORIAL quests: " + e.getMessage(), e);
      }

      // 각 퀘스트를 사용자 퀘스트로 등록
      for (QuestDTO questDTO : tutorial) {
        try {
          userQuestService.registerUserQuest(
              new UserQuestRegisterRequestDTO(questDTO.getQuestId(), savedUser.getId()));
        } catch (Exception e) {
          // 개별 퀘스트 등록 실패 시 로그 남기기
          System.err.println(
              "Failed to register quest for user: " + questDTO + ", Error: " + e.getMessage());
        }
      }

      //== 유저 생성할 때 인벤토리 생성 ==//
      inventoryService.createInventory(savedUser.getId());

      // 유저 DTO 반환
      return new UserDTO(savedUser);

    } catch (Exception e) {
      // 전체 프로세스에서 발생한 예외 처리
      System.err.println("Failed to register user: " + e.getMessage());
      throw new RuntimeException("User registration failed: " + e.getMessage(), e);
    }
  }


  @Transactional
  public UserDTO updateUser(UserUpdateDTO dto) {
    User foundUser = userRepository.findById(dto.getId()).orElseThrow(
        () -> new IllegalStateException("not found user id:" + dto.getId())
    );

    if (dto.getName() != null) {
      foundUser.setName(dto.getName());
    }
    if (dto.getGrade() != null) {
      foundUser.setGrade(dto.getGrade());
    }
    if (dto.getBirthday() != null) {
      foundUser.setBirthday(dto.getBirthday());
    }
    if (dto.getGender() != null) {
      foundUser.setGender(dto.getGender());
    }
    if (dto.getPhone() != null) {
      foundUser.setPhone(dto.getPhone());
    }
    if(dto.getInterest() != null){
      foundUser.setInterest(new ArrayList<>(dto.getInterest()));
      aiRecommendationService.sendInterestToAI(new UserRecomendByInterestRequestDTO(foundUser));
    }
    if(dto.getStatusMesasge() != null) {
      foundUser.setStatusMessage(dto.getStatusMesasge());
    }
    if(dto.getGold() != null) {
      foundUser.setGold(dto.getGold());
    }
    if(dto.getSchoolId() != null){
      School school = schoolRepository.findById(dto.getSchoolId()).orElseThrow(() -> new IllegalStateException("not found school id:" + dto.getSchoolId()));
      foundUser.setSchool(school);
    }

    return new UserDTO(foundUser);
  }

  @Transactional
  public void removeUser(Integer id) {
    User user = userRepository.findById(id).orElseThrow(
        () -> new IllegalStateException("not found user id:" + id));

    // 친구 추천 정보 삭제
    aiRecommendationService.deleteRecommendationListByUserId(id);

    // 아바타 삭제
    if(avatarService.isExistAvatar(id)) {
      avatarService.deleteAvatar(id);
    }

    // 게시판 삭제
    boardService.deleteBoardListByUserId(id);

    // 게시판 좋아요 로그 삭제
    boardLikeService.deleteBoardLikeListByUserId(id);

    // 챗봇 로그 삭제
    chatBotLogService.deleteChatBotLogListByUserId(id);

    // 채팅 삭제
    chatLogService.deleteChatLogListByUserId(id);

    // 댓글 삭제 -> 게시판 삭제 될 때 같이 삭제됨.

    // 친구 관계 삭제
    friendshipService.deleteFriendshipListByUserId(id);

    // 유저가 설치한 가구 삭제
    furnitureService.deleteFurnitureListByUserId(id);

    // 갤러리 삭제
//    galleryService.deleteGalleryListByUserId(id);

    // 쪽지 삭제
    guestBookService.deleteGuestBookListByUserId(id);

    // 인벤토리 삭제
    inventoryService.deleteInventoryByUserId(id);

    // 맵콘테스트 삭제
//    if(!mapContestService.getMapContestListByUserId(id).isEmpty()) {
//      mapContestService.deleteMapContestListByUserId(id);
//    }

    // 맵콘테스트 가구 리스트는 맵 콘테스트 삭제되면서 같이 삭제됨.

    // 쪽지 삭제
    noteService.deleteNoteListByUserId(id);

    // 유저 방문 횟수 db 삭제
    userPosVisitCountService.deleteUserPosVisitCountListByUserId(id);

    // 유저 퀘스트 DB 삭제
    userQuestService.deleteUserQuestListByUserId(id);

    // 삭제하려는 유저의 학교에 존재하는 유저 리스트에서 해당 유저를 제거
    if(user.getSchool() != null) {
      schoolService.deleteUserInUserList(user.getSchool().getId(), id);
    }

    // 감정 분석 로그 삭제
    emotionAnalysisService.deleteUserLogsByUserId(id);

    userRepository.deleteById(id);
  }

  public void gainExp(Integer id, Integer exp) {
    User foundUser = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("not found user id:" + id));
    foundUser.gainExp(exp);
    userRepository.save(foundUser);
  }

  public boolean existUserDatas() {
    return userRepository.count() > 0;
  }

  @Transactional
  public User createUser(UserRegisterDTO dto) {
    User user = new User();
    user.setName(dto.getName());
    user.setGrade(dto.getGrade());
    user.setBirthday(dto.getBirthday());
    user.setGender(dto.getGender());
    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword());
    user.setPhone(dto.getPhone());
    user.setInterest(new ArrayList<>(dto.getInterest()));
    user.setStatusMessage(dto.getStatusMessage());
    user.setGold(100000);

    user.setLevel(1);
    user.setExp(0);
    user.setMaxExp(100);

    // SchoolService를 사용해 schoolId로 School 객체를 조회
    if(dto.getSchoolId() != null) {
      School school = schoolRepository.findById(dto.getSchoolId()).orElse(null);
      user.setSchool(school); // 조회한 School 객체를 User에 설정
    }

    User savedUser = userRepository.save(user);// 저장

    savedUser.setMapId(savedUser.getId());
    savedUser.setMapType("MyClassroom");

    return savedUser;
  }

  @Transactional
  public UserDTO registerSchoolToUser(UserRegisterSchoolDTO dto) {
    User foundUser = userRepository.findById(dto.getUserId()).orElseThrow(() -> new IllegalStateException("not found registerSchoolForUser id:" + dto.getUserId()));
    School foundSchool = schoolRepository.findById(dto.getSchoolId()).orElseThrow(() -> new IllegalStateException("not found school id:" + dto.getSchoolId()));
    foundUser.setSchool(foundSchool);
    return new UserDTO(foundUser);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByUserEmail(email);
  }

  public UserProfileDTO getProfile(Integer userId) {
    return new UserProfileDTO(userRepository.findById(userId).orElseThrow(
        () -> new IllegalStateException("not found user id : " + userId))
    );
  }

  @Transactional
  public UserProfileDTO updateProfile(UserProfileDTO dto) {
    User foundUser = userRepository.findById(dto.getId()).orElseThrow(
        () -> new IllegalArgumentException("not found user id : " + dto.getId()));

    foundUser.setName(dto.getName());
    foundUser.setInterest(dto.getInterest());
    foundUser.setStatusMessage(dto.getStatusMessage());

    return dto;
  }

  @Transactional
  public void setAllUserStatusToOffline() {
    List<User> userList = userRepository.findAll();
    userList.forEach(
        user -> {
          user.setIsOnline(false);
          userRepository.save(user);
        }
    );
  }

  @Transactional
  public void setUserStatusToOnline(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("not found user id : " + userId));

    // 현재 날짜 및 시간을 ISO 8601 형식으로 포맷
    DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
    String formattedDate = LocalDateTime.now().format(isoFormatter);

    user.setEnteredDate(formattedDate);
    user.setIsOnline(true);
  }

  @Transactional
  public void setUserStatusToOffline(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("not found user id : " + userId));

    user.setIsOnline(false);
  }

  public UserPosDTO getPosition(Integer userId) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new IllegalStateException("not found user id : " + userId));
    return new UserPosDTO(user);
  }

  @Transactional
  public ResponseResult<UserPosDTO> updatePosition(UserPosUpdateDTO dto) {
    User user = userRepository.findById(dto.getUserId()).orElseThrow(
        () -> new IllegalStateException("not found user id : " + dto.getUserId()));

    if(dto.getMapId() != null) {
      user.setMapId(dto.getMapId());
    }

    if(dto.getMapType() != null){
      user.setMapType(dto.getMapType());
    }

    return success(new UserPosDTO(user));
  }

  public Boolean isUserExistByUserEmail(String userEmail) {
    return userRepository.getUserByEmail(userEmail) != null;
  }
}
