package com.project.final_project.avatar.controller;

import com.project.final_project.avatar.dto.AvatarRequestDTO;
import com.project.final_project.avatar.dto.AvatarResponseDTO;
import com.project.final_project.avatar.dto.AvatarUpdateDTO;
import com.project.final_project.avatar.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {

  private final AvatarService avatarService;

  @PostMapping
  public ResponseEntity<AvatarResponseDTO> registerAvatar(@RequestBody AvatarRequestDTO avatarRequestDTO) {
    return ResponseEntity.ok(avatarService.registerAvatar(avatarRequestDTO));
  }

  @GetMapping
  public ResponseEntity<AvatarResponseDTO> getAvatarByUserId(@RequestParam("userId") Integer userId) {
    return ResponseEntity.ok(avatarService.getAvatarByUserId(userId));
  }

  @PatchMapping
  public ResponseEntity<AvatarResponseDTO> updateAvatar(@RequestBody AvatarUpdateDTO avatarUpdateDTO) {
    return ResponseEntity.ok(avatarService.updateAvatar(avatarUpdateDTO));
  }

  @DeleteMapping
  public ResponseEntity<?> deleteAvatar(@RequestParam("userId") Integer userId) {
    avatarService.deleteAvatar(userId);
    return ResponseEntity.noContent().build();
  }

}
