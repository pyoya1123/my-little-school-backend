package com.project.final_project.quest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.project.final_project.item.domain.Item;
import com.project.final_project.item.repository.ItemRepository;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.dto.quest.QuestDTO;
import com.project.final_project.quest.dto.quest.QuestRegisterRequestDTO;
import com.project.final_project.quest.dto.quest.QuestRewardRequestDTO;
import com.project.final_project.quest.repository.QuestRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {

  @InjectMocks
  private QuestService questService;

  @Mock
  private QuestRepository questRepository;

  @Mock
  private ItemRepository itemRepository;

  @Test
  @DisplayName("퀘스트 등록 성공 - 아이템 보상 포함")
  void registerQuest_Success() {
    // Given
    QuestRewardRequestDTO rewardRequest = new QuestRewardRequestDTO(1, 5); // 아이템ID 1번, 5개
    QuestRegisterRequestDTO requestDTO = new QuestRegisterRequestDTO(
        "테스트 퀘스트", "내용", "DAILY", 1, 100, 50, List.of(rewardRequest)
    );

    Item mockItem = new Item();
    mockItem.setId(1);
    mockItem.setName("테스트 아이템");

    Quest savedQuest = new Quest("테스트 퀘스트", "내용", 1, "DAILY", 100, 50);
    savedQuest.setId(1);

    // Mocking 동작 정의
    given(itemRepository.findById(1)).willReturn(Optional.of(mockItem));
    given(questRepository.save(any(Quest.class))).willReturn(savedQuest);

    // When
    QuestDTO result = questService.registerQuest(requestDTO);

    // Then
    assertThat(result.getTitle()).isEqualTo("테스트 퀘스트");
    verify(itemRepository).findById(1); // 아이템 조회가 호출되었는지 검증
    verify(questRepository).save(any(Quest.class)); // 저장이 호출되었는지 검증
  }
}

