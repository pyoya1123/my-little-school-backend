package com.project.final_project.quest.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.quest.domain.QuestItemRewardInfo;
import com.project.final_project.quest.dto.questitemrewardinfo.QuestItemRewardInfoDTO;
import com.project.final_project.quest.dto.questitemrewardinfo.QuestItemRewardInfoRegisterDTO;
import com.project.final_project.quest.service.QuestItemRewardInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest-item-reward-info")
public class QuestItemRewardInfoController {

  private final QuestItemRewardInfoService questItemRewardInfoService;

  @PostMapping
  public ResponseResult<QuestItemRewardInfoDTO> addQuestItemRewardInfo(@RequestBody QuestItemRewardInfoRegisterDTO dto) {
    return questItemRewardInfoService.addQuestItemRewardInfo(dto);
  }

  @GetMapping("/list/{questId}")
  public List<QuestItemRewardInfoDTO> getQuestItemRewardInfoListByQuestId(@PathVariable("questId") Integer questId) {
    return questItemRewardInfoService.getQuestItemRewardInfoListByQuestId(questId);
  }

  @GetMapping("/{questItemRewardInfoId}")
  public QuestItemRewardInfoDTO getQuestItemRewardInfoById(
      @PathVariable("questItemRewardInfoId") Integer id) {
    return questItemRewardInfoService.getQuestItemRewardInfoById(id);
  }

  @PatchMapping
  public ResponseResult<QuestItemRewardInfoDTO> updateQuestItemRewardInfo(
      @RequestBody QuestItemRewardInfoDTO dto) {
    return questItemRewardInfoService.updateQuestItemRewardInfo(dto);
  }

  @DeleteMapping("/{questItemRewardInfoId}")
  public ResponseResult<String> deleteQuestItemRewardInfo(@PathVariable("questItemRewardInfoId") Integer id) {
    questItemRewardInfoService.deleteQuestItemRewardInfo(id);
    return success("id : " + id + " 인 퀘스트 아이템 보상 정보를 삭제했습니다.");
  }
}
