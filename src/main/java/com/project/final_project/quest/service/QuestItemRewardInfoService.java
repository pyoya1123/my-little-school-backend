package com.project.final_project.quest.service;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.quest.domain.Quest;
import com.project.final_project.quest.domain.QuestItemRewardInfo;
import com.project.final_project.quest.dto.questitemrewardinfo.QuestItemRewardInfoDTO;
import com.project.final_project.quest.dto.questitemrewardinfo.QuestItemRewardInfoRegisterDTO;
import com.project.final_project.quest.repository.QuestItemRewardInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestItemRewardInfoService {

  private final QuestItemRewardInfoRepository questItemRewardInfoRepository;

  public ResponseResult<QuestItemRewardInfoDTO> addQuestItemRewardInfo(
      QuestItemRewardInfoRegisterDTO dto) {

    QuestItemRewardInfo questItemRewardInfo =
        new QuestItemRewardInfo(dto.getQuestId(), dto.getItemIdx(), dto.getCount());

    return success(new QuestItemRewardInfoDTO(questItemRewardInfoRepository.save(questItemRewardInfo)));
  }

  public List<QuestItemRewardInfoDTO> getQuestItemRewardInfoListByQuestId(Integer questId) {
    return questItemRewardInfoRepository.getQuestItemRewardInfoListByQuestId(questId)
        .stream().map(QuestItemRewardInfoDTO::new).toList();
  }

  public QuestItemRewardInfoDTO getQuestItemRewardInfoById(Integer id) {
    return new QuestItemRewardInfoDTO(questItemRewardInfoRepository.findById(id).orElseThrow(
        () -> new IllegalStateException("not found questItemRewardInfo id : " + id)));
  }

  @Transactional
  public ResponseResult<QuestItemRewardInfoDTO> updateQuestItemRewardInfo(
      QuestItemRewardInfoDTO dto) {

    QuestItemRewardInfo questItemRewardInfo = questItemRewardInfoRepository.findById(
        dto.getQuestItemRewardInfoId()).orElseThrow(
        () -> new IllegalStateException(
            "not found questItemRewardInfo id : " + dto.getQuestItemRewardInfoId()));

    if(dto.getQuestId() != null) {
      questItemRewardInfo.setQuestId(dto.getQuestId());
    }

    if(dto.getItemIdx() != null) {
      questItemRewardInfo.setItemIdx(dto.getItemIdx());
    }

    if(dto.getCount() != null){
      questItemRewardInfo.setItemCount(dto.getCount());
    }

    return success(new QuestItemRewardInfoDTO(questItemRewardInfoRepository.save(questItemRewardInfo)));
  }

  public void deleteQuestItemRewardInfo(Integer id) {
    questItemRewardInfoRepository.deleteById(id);
  }
}
