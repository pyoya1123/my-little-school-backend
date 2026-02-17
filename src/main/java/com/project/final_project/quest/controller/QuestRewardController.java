package com.project.final_project.quest.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.quest.dto.questReward.QuestRewardDTO;
import com.project.final_project.quest.service.QuestRewardService;
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
@RequestMapping("/quest-reward")
public class QuestRewardController {

  private final QuestRewardService questRewardService;

//  @PostMapping
//  public ResponseResult<QuestRewardDTO> addQuestReward(@RequestBody QuestRewardRegisterDTO dto) {
//    return questRewardService.addQuestReward(dto);
//  }
//
//  @GetMapping("/list/{questId}")
//  public List<QuestRewardDTO> getQuestRewardListByQuestId(@PathVariable("questId") Integer questId) {
//    return questRewardService.getQuestRewardListByQuestId(questId);
//  }
//
//  @GetMapping("/{QuestRewardId}")
//  public QuestRewardDTO getQuestRewardById(
//      @PathVariable("QuestRewardId") Integer id) {
//    return questRewardService.getQuestRewardById(id);
//  }

//  @PatchMapping
//  public ResponseResult<QuestRewardDTO> updateQuestReward(
//      @RequestBody QuestRewardDTO dto) {
//    return questRewardService.updateQuestReward(dto);
//  }

  @DeleteMapping("/{QuestRewardId}")
  public ResponseResult<String> deleteQuestReward(@PathVariable("QuestRewardId") Integer id) {
    questRewardService.deleteQuestReward(id);
    return success("id : " + id + " 인 퀘스트 아이템 보상 정보를 삭제했습니다.");
  }
}
