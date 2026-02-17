package com.project.final_project.quest.service;

import com.project.final_project.item.domain.Item;
import com.project.final_project.item.repository.ItemRepository;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.QuestReward;
import com.project.final_project.quest.dto.quest.QuestDTO;
import com.project.final_project.quest.dto.quest.QuestRegisterRequestDTO;
import com.project.final_project.quest.dto.quest.QuestRewardRequestDTO;
import com.project.final_project.quest.dto.quest.QuestUpdateRequestDTO;
import com.project.final_project.quest.repository.QuestRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestService {

  private final QuestRepository questRepository;
  private final ItemRepository itemRepository;

  public QuestDTO registerQuest(QuestRegisterRequestDTO dto) {

    Quest newQuest = new Quest(dto.getTitle(), dto.getContent(), dto.getCount(),
        dto.getQuestType(), dto.getGold(), dto.getExp());

    if (dto.getRewardInfo() != null) {
      for (QuestRewardRequestDTO rewardDto : dto.getRewardInfo()) {
        Item item = itemRepository.findById(rewardDto.getItemIdx())
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + rewardDto.getItemIdx()));

        QuestReward reward = new QuestReward(newQuest, item, rewardDto.getCount());
        newQuest.getRewards().add(reward);
      }
    }

    return new QuestDTO(questRepository.save(newQuest));
  }

  public void saveQuest(Quest quest) {
    questRepository.save(quest);
  }

  @Transactional
  public void deleteQuestByQuestId(Integer questId) {
    questRepository.deleteById(questId);
  }

  @Transactional
  public QuestDTO updateQuest(QuestUpdateRequestDTO dto) {
    Quest quest = questRepository.findById(dto.getQuestId()).orElseThrow(
        () -> new IllegalStateException("not found quest id : " + dto.getQuestId()));

    if(dto.getTitle() != null){
      quest.setTitle(dto.getTitle());
    }

    if(dto.getContent() != null){
      quest.setContent(dto.getContent());
    }

    if(dto.getQuestType() != null) {
      quest.setQuestType(dto.getQuestType());
    }

    if(dto.getGold() != null) {
      quest.setGold(dto.getGold());
    }

    if(dto.getExp() != null) {
      quest.setExp(dto.getExp());
    }

    return new QuestDTO(quest);
  }

  public List<QuestDTO> getAllQuest() {
    return questRepository.findAll().stream().map(QuestDTO::new).toList();
  }

  public List<QuestDTO> getQuestListByQuestType(String questType) {
    return questRepository.getQuestListByQuestType(questType).stream().map(QuestDTO::new).toList();
  }

  public boolean existsQuests() {
    return !questRepository.findAll().isEmpty();
  }
}
