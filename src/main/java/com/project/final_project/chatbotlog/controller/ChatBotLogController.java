package com.project.final_project.chatbotlog.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.chatbotlog.dto.ChatBotLogDTO;
import com.project.final_project.chatbotlog.dto.ChatBotLogUserRequestDTO;
import com.project.final_project.chatbotlog.service.ChatBotLogService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-bot-log")
public class ChatBotLogController {

  private final ChatBotLogService chatBotLogService;

  @PostMapping
  public ChatBotLogDTO requestToChatBot(@RequestBody ChatBotLogUserRequestDTO dto) {
    return chatBotLogService.requestToChatBot(dto);
  }

  @GetMapping("/{userId}")
  public List<ChatBotLogDTO> getChatBotLogListByUserId(@PathVariable("userId") Integer userId){
    return chatBotLogService.getChatBotLogListByUserId(userId);
  }
}
