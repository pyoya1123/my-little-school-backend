package com.project.final_project.airecommendation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendResponseDTO {

  private Integer recommendedUserId; // 추천된 유저 아이디
  private String username;           // 사용자 이름
  private Double similarityValue;     // 유사도 값
  private Boolean onlineStatus;       // 온라인 상태
  private String recommendationLevel; // 추천 등급
  private Integer grade;          // 사용자 등급
  private String schoolLocation;        // 학교 위치
  private List<String> interests;     // 관심사 목록
  private String similarityMessage;   // 유사도 관련 메시지
  private String avatarImageUrl; // 아바타 이미지 URL

}
