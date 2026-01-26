package com.project.final_project.schedule.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.schedule.service.ScheduleService;
import com.project.final_project.user.dto.UserRequestOnlineStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

  private final ScheduleService scheduleService;

  @PostMapping("/request-online-status")
  public ResponseResult<String> requestOnlineStatus(@RequestBody UserRequestOnlineStatusDTO dto) {
    scheduleService.addUserStatusToMap(dto);
    return success("user id : " + dto.getUserId() + " set online");
  }

}
