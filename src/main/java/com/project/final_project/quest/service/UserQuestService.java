package com.project.final_project.quest.service;

import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.UserQuest;
import com.project.final_project.quest.dto.userquest.AllUserQuestListResponseDTO;
import com.project.final_project.quest.dto.userquest.UserQuestDTO;
import com.project.final_project.quest.dto.userquest.UserQuestRegisterRequestDTO;
import com.project.final_project.quest.dto.userquest.UserQuestUpdateDTO;
import com.project.final_project.quest.repository.QuestRepository;
import com.project.final_project.quest.repository.UserQuestRepository;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQuestService {

  private final UserQuestRepository userQuestRepository;
  private final UserRepository userRepository;
  private final QuestRepository questRepository;

  public Boolean isUserQuestComplete(Integer questId) {
    try {
      return userQuestRepository.getUserQuestByQuestId(questId).getIsComplete();
    } catch (NullPointerException | NoSuchElementException e) {
      // 특정 예외 처리: 데이터가 없는 경우
      throw new IllegalStateException("User quest not found for questId: " + questId, e);
    } catch (Exception e) {
      // 기타 예외 처리
      throw new IllegalStateException("Unexpected error occurred while fetching user quest", e);
    }
  }

  public List<UserQuestDTO> getCompletedUserQuestByUserId(Integer userId) {
    return userQuestRepository.getCompletedUserQuestListByUserId(userId)
        .stream().map(UserQuestDTO::new).toList();
  }

  public List<UserQuestDTO> getNotCompletedUserQuestByUserId(Integer userId) {
    return userQuestRepository.getNotCompletedUserQuestListByUserId(userId)
        .stream().map(UserQuestDTO::new).toList();
  }

  public List<AllUserQuestListResponseDTO> getAllUserQuestByUserId(Integer userId) {
    return userQuestRepository.getUserQuestListByUserId(userId)
        .stream().map(AllUserQuestListResponseDTO::new).toList();
  }

  @Transactional
  public UserQuestDTO updateUserQuest(UserQuestUpdateDTO dto) {
    // UserQuest 조회
    UserQuest userQuest = userQuestRepository.findById(dto.getUserQuestId())
        .orElseThrow(() -> new IllegalStateException("not found quest id: " + dto.getUserQuestId()));

    // isComplete 업데이트
    if (dto.getIsComplete() != null) {
      userQuest.setIsComplete(dto.getIsComplete());
    }

    // User 업데이트
    Optional.ofNullable(dto.getUserId()).ifPresent(userId -> {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalStateException("not found user id: " + userId));
      userQuest.setUser(user);
    });

    // Quest 업데이트
    Optional.ofNullable(dto.getQuestId()).ifPresent(questId -> {
      Quest quest = questRepository.findById(questId)
          .orElseThrow(() -> new IllegalStateException("not found quest id: " + questId));
      userQuest.setQuest(quest);
    });

    // Count 업데이트
    if (dto.getCount() != null) {
      userQuest.setCount(dto.getCount());
    }

    // 변경된 UserQuest 정보를 DTO로 반환
    return new UserQuestDTO(userQuest);
  }


  public UserQuestDTO registerUserQuest(UserQuestRegisterRequestDTO dto) {
    User foundUser = userRepository.findById(dto.getUserId()).orElseThrow(
        () -> new IllegalStateException("not found user id : " + dto.getUserId()));

    Quest foundQuest = questRepository.findById(dto.getQuestId()).orElseThrow(
        () -> new IllegalStateException("not found quest id : " + dto.getQuestId()));

    UserQuest uq = new UserQuest(foundUser, foundQuest, foundQuest.getCount(), false);

    return new UserQuestDTO(userQuestRepository.save(uq));
  }

  @Transactional
  public UserQuestDTO completeUserQuest(Integer questId) {
    UserQuest userQuest = userQuestRepository.findById(questId).orElseThrow(
        () -> new IllegalStateException("not found quest id : " + questId));

    if(userQuest.getIsComplete() || userQuest.getCount() <= 0) {
      throw new IllegalStateException("이미 완료된 퀘스트입니다.");
    }

    userQuest.setCount(userQuest.getCount() - 1);
    if(userQuest.getCount() <= 0) {
      userQuest.setIsComplete(true);
    }

    return new UserQuestDTO(userQuest);
  }

  public UserQuestDTO getUserQuestByQuestIdAndUserId(Integer questId, Integer userId) {
    return new UserQuestDTO(userQuestRepository.getUserQuestByQuestIdAndUserId(questId, userId));
  }

  @Transactional
  public void deleteUserQuestListByUserId(Integer userId) {
    userQuestRepository.deleteAll(userQuestRepository.getUserQuestListByUserId(userId));
  }
}
