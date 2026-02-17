package com.project.final_project.quest.service;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.item.domain.Item;
import com.project.final_project.item.repository.ItemRepository;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.QuestReward;
import com.project.final_project.quest.dto.questReward.QuestRewardDTO;
import com.project.final_project.quest.dto.questReward.QuestRewardRegisterDTO;
import com.project.final_project.quest.repository.QuestRepository;
import java.util.List;

import com.project.final_project.quest.repository.QuestRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestRewardService {

  private final QuestRewardRepository questRewardRepository;
  private final QuestRepository questRepository;
  private final ItemRepository itemRepository;

  public ResponseResult<QuestRewardDTO> addQuestReward(
      QuestRewardRegisterDTO dto) {

    Quest quest = questRepository.findById(dto.getQuestId())
        .orElseThrow(() -> new IllegalStateException("not found quest id : " + dto.getQuestId()));
    
    Item item = itemRepository.findById(dto.getItemIdx())
        .orElseThrow(() -> new IllegalStateException("not found item id : " + dto.getItemIdx()));

    QuestReward questReward = new QuestReward(quest, item, dto.getCount());

    return success(new QuestRewardDTO(questRewardRepository.save(questReward)));
  }

  public List<QuestRewardDTO> getQuestRewardListByQuestId(Integer questId) {
    return questRewardRepository.getQuestRewardListByQuestId(questId)
        .stream().map(QuestRewardDTO::new).toList();
  }

  public QuestRewardDTO getQuestRewardById(Integer id) {
    return new QuestRewardDTO(questRewardRepository.findById(id).orElseThrow(
        () -> new IllegalStateException("not found QuestReward id : " + id)));
  }

  @Transactional
  public ResponseResult<QuestRewardDTO> updateQuestReward(
      QuestRewardDTO dto) {

    QuestReward questReward = questRewardRepository.findById(
        dto.getQuestRewardId()).orElseThrow(
        () -> new IllegalStateException(
            "not found QuestReward id : " + dto.getQuestRewardId()));

    if(dto.getQuestId() != null) {
      Quest quest = questRepository.findById(dto.getQuestId())
          .orElseThrow(() -> new IllegalStateException("not found quest id : " + dto.getQuestId()));
      questReward.setQuest(quest);
    }

    if(dto.getItemIdx() != null) {
      Item item = itemRepository.findById(dto.getItemIdx())
          .orElseThrow(() -> new IllegalStateException("not found item id : " + dto.getItemIdx()));
      questReward.setItem(item);
    }

    if(dto.getCount() != null){
      questReward.setItemCount(dto.getCount());
    }

    return success(new QuestRewardDTO(questRewardRepository.save(questReward)));
  }

  public void deleteQuestReward(Integer id) {
    questRewardRepository.deleteById(id);
  }
}
