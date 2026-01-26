package com.project.final_project.quest.controller;

import com.project.final_project.quest.dto.quest.QuestDTO;
import com.project.final_project.quest.dto.quest.QuestRegisterRequestDTO;
import com.project.final_project.quest.dto.quest.QuestUpdateRequestDTO;
import com.project.final_project.quest.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
public class QuestController {

  private final QuestService questService;


  @Operation(summary = "각 유저에 대해 퀘스트 등록", description = "특정 유저에 대한 퀘스트를 등록합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "퀘스트가 성공적으로 등록되었습니다."),
      @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않습니다.")
  })
  @PostMapping
  public QuestDTO registerQuest(@RequestBody QuestRegisterRequestDTO dto) {
    return questService.registerQuest(dto);
  }


  @Operation(summary = "모든 퀘스트 목록 조회", description = "등록된 모든 퀘스트 목록을 반환합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "모든 퀘스트 목록을 반환합니다.")
  })
  @GetMapping("/list")
  public List<QuestDTO> getAllQuest() {
    return questService.getAllQuest();
  }


  @GetMapping("/list/{questType}")
  public List<QuestDTO> getQuestListByQuestType(@PathVariable("questType") String questType) {
    return questService.getQuestListByQuestType(questType);
  }


  @Operation(summary = "퀘스트 삭제", description = "특정 퀘스트를 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "퀘스트가 성공적으로 삭제되었습니다."),
      @ApiResponse(responseCode = "404", description = "퀘스트를 찾을 수 없습니다.")
  })
  @DeleteMapping("/{questId}")
  public ResponseEntity<?> deleteQuestByQuestId(@PathVariable("questId") Integer questId) {
    questService.deleteQuestByQuestId(questId);
    return ResponseEntity.noContent().build();
  }


  @Operation(summary = "퀘스트 업데이트", description = "특정 퀘스트의 정보를 업데이트합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "퀘스트가 성공적으로 업데이트되었습니다."),
      @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않습니다."),
      @ApiResponse(responseCode = "404", description = "퀘스트를 찾을 수 없습니다.")
  })
  @PatchMapping
  public QuestDTO updateQuest(@RequestBody QuestUpdateRequestDTO dto) {
    return questService.updateQuest(dto);
  }
}
