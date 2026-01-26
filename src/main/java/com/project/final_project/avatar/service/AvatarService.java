package com.project.final_project.avatar.service;

import com.project.final_project.avatar.domain.Avatar;
import com.project.final_project.avatar.dto.AvatarRequestDTO;
import com.project.final_project.avatar.dto.AvatarResponseDTO;
import com.project.final_project.avatar.dto.AvatarUpdateDTO;
import com.project.final_project.avatar.repository.AvatarRepository;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvatarService {

  private final AvatarRepository avatarRepository;
  private final UserRepository userRepository;

  public AvatarResponseDTO registerAvatar(AvatarRequestDTO avatarRequestDTO) {

    User user = userRepository.findById(avatarRequestDTO.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("not found user id : " + avatarRequestDTO.getUserId()));

    Avatar foundAvatar = avatarRepository.getAvatarByUserId(user.getId()).orElse(null);

    if(foundAvatar != null){
      throw new IllegalStateException("이미 아바타가 존재합니다.");
    }

    Avatar avatar = Avatar.builder()
        .userId(user.getId())
        .infoList(avatarRequestDTO.getInfoList())
        .build();

    Avatar savedAvatar = avatarRepository.save(avatar);
    return new AvatarResponseDTO(savedAvatar.getId(), savedAvatar.getInfoList());
  }

  public AvatarResponseDTO getAvatarByUserId(Integer userId) {
    Avatar foundAvatar = avatarRepository.getAvatarByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("not found user id : " + userId));
    return new AvatarResponseDTO(foundAvatar.getId(), foundAvatar.getInfoList());
  }

  @Transactional
  public AvatarResponseDTO updateAvatar(AvatarUpdateDTO avatarUpdateDTO) {
    Avatar foundAvatar = avatarRepository.getAvatarByUserId(avatarUpdateDTO.getUserId())
        .orElseThrow(
            () -> new IllegalArgumentException("not found avatar userid : " + avatarUpdateDTO.getUserId())
        );
    foundAvatar.setInfoList(avatarUpdateDTO.getInfoList());
//    avatarRepository.save(foundAvatar);
    return new AvatarResponseDTO(foundAvatar.getId(), foundAvatar.getInfoList());
  }

  @Transactional
  public void deleteAvatar(Integer userId) {

    Avatar avatar = avatarRepository.getAvatarByUserId(userId).orElseThrow(
        () -> new IllegalArgumentException("not found avartar userid : " + userId));

    avatar.getInfoList().clear();

    avatarRepository.delete(avatar);
  }

  public boolean isExistAvatar(Integer userId) {
    return avatarRepository.getAvatarByUserId(userId).isPresent();
  }
}
