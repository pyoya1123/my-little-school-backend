package com.project.final_project.chatlog.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.chatlog.dto.ChatLogRequestDTO;
import com.project.final_project.chatlog.dto.ChatLogResponseDTO;
import com.project.final_project.chatlog.service.ChatLogService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-log")
@RequiredArgsConstructor
@Slf4j
public class ChatLogController {

  private final ChatLogService chatLogService;

  @Operation(summary = "채팅 로그 저장", description = "새로운 채팅 로그를 저장합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채팅 로그가 성공적으로 저장되었습니다."),
      @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
  })
  @PostMapping
  public ResponseEntity<ChatLogResponseDTO> saveChatLog(@RequestBody ChatLogRequestDTO chatLogRequestDTO) {
    return ResponseEntity.ok(chatLogService.saveChatLog(chatLogRequestDTO));
  }

  @Operation(summary = "특정 발신자의 채팅 로그 조회", description = "발신자의 ID를 기반으로 채팅 로그를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채팅 로그 목록이 성공적으로 조회되었습니다."),
      @ApiResponse(responseCode = "404", description = "해당 발신자의 채팅 로그를 찾을 수 없습니다.")
  })
  @GetMapping
  public ResponseEntity<List<ChatLogResponseDTO>> getChatLogsBySenderId(@RequestParam("senderId") Integer senderId) {
    return ResponseEntity.ok(chatLogService.getChatLogsBySenderId(senderId));
  }

  @Operation(summary = "전체 채팅 로그 조회", description = "모든 채팅 로그를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 채팅 로그가 성공적으로 조회되었습니다.")
  })
  @GetMapping("/list")
  public ResponseResult<List<ChatLogResponseDTO>> getAllChatLogs() {
    List<ChatLogResponseDTO> allChatLogs = chatLogService.getAllChatLogs();
    return success(allChatLogs);
  }
}
