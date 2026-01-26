package com.project.final_project.airecommendation.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AIResponseDTO {

  // 내부 클래스 for recommended users
  @Getter
  @Setter
  public static class RecommendedUser {
    private Integer senderId; // 추천된 유저 아이디
    private String message; // 검사했을 때, 유사했던 메시지
    private Double similarity;  // np.float64는 Java에서 Double로 매핑
    private List<String> interests;

    @Override
    public String toString() {
      return "RecommendedUser{" +
          "senderId=" + senderId +
          ", message='" + message + '\'' +
          ", similarity=" + similarity +
          ", interests=" + interests +
          '}';
    }
  }

  // recommended_users 필드
  private List<RecommendedUser> recommended_users;

  @Override
  public String toString() {
    return "AIResponseDTO{" +
        "recommended_users=" + recommended_users +
        '}';
  }
}
