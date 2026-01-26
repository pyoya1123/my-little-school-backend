package com.project.final_project.quest.controller;

import com.project.final_project.quest.dto.userquest.AllUserQuestListResponseDTO;
import com.project.final_project.quest.dto.userquest.UserQuestDTO;
import com.project.final_project.quest.dto.userquest.UserQuestRegisterRequestDTO;
import com.project.final_project.quest.dto.userquest.UserQuestUpdateDTO;
import com.project.final_project.quest.service.UserQuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-quest")
public class UserQuestController {

  private final UserQuestService userQuestService;

  @PostMapping
  public UserQuestDTO registerUserQuest(@RequestBody UserQuestRegisterRequestDTO dto) {
    return userQuestService.registerUserQuest(dto);
  }


  @Operation(summary = "퀘스트 완료 여부 확인", description = "사용자가 특정 퀘스트를 완료했는지 확인합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "퀘스트 완료 여부를 반환합니다."),
      @ApiResponse(responseCode = "404", description = "퀘스트 또는 사용자를 찾을 수 없습니다.")
  })
  @GetMapping("/is-complete/{questId}")
  public Boolean isUserQuestComplete(@PathVariable("questId") Integer questId) {
    return userQuestService.isUserQuestComplete(questId);
  }


  @Operation(summary = "완료된 퀘스트 목록 조회", description = "특정 사용자가 완료한 퀘스트 목록을 반환합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "완료된 퀘스트 목록을 반환합니다."),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
  })
  @GetMapping("/list/completed/{userId}")
  public List<UserQuestDTO> getCompletedUserQuestByUserId(@PathVariable("userId") Integer userId) {
    return userQuestService.getCompletedUserQuestByUserId(userId);
  }


  @Operation(summary = "사용자별 퀘스트 목록 조회", description = "특정 사용자가 등록된 모든 퀘스트 목록을 반환합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "사용자별 퀘스트 목록을 반환합니다."),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
  })
  @GetMapping("/list/{userId}")
  public List<AllUserQuestListResponseDTO> getAllUserQuestByUserId(@PathVariable("userId") Integer userId) {
    return userQuestService.getAllUserQuestByUserId(userId);
  }

  @GetMapping("/{questId}/{userId}")
  public UserQuestDTO getUserQuestByQuestIdAndUserId(
      @PathVariable("questId") Integer questId,
      @PathVariable("userId") Integer userId) {
    return userQuestService.getUserQuestByQuestIdAndUserId(questId, userId);
  }


  @Operation(summary = "미완료된 퀘스트 목록 조회", description = "특정 사용자가 완료하지 않은 퀘스트 목록을 반환합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "미완료된 퀘스트 목록을 반환합니다."),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
  })
  @GetMapping("/list/not-completed/{userId}")
  public List<UserQuestDTO> getNotCompletedUserQuestByUserId(@PathVariable("userId") Integer userId) {
    return userQuestService.getNotCompletedUserQuestByUserId(userId);
  }

  @PostMapping("/complete-quest/{questId}")
  public UserQuestDTO completeUserQuest(@PathVariable("questId") Integer questId) {
    return userQuestService.completeUserQuest(questId);
  }

  @PatchMapping
  public UserQuestDTO updateUserQuest(@RequestBody UserQuestUpdateDTO dto){
    return userQuestService.updateUserQuest(dto);
  }
}
