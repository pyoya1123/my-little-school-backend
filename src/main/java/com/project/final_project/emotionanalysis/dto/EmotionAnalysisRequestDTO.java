package com.project.final_project.emotionanalysis.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionAnalysisRequestDTO {
  Integer senderId;
  List<String> messages = new ArrayList<>();

  public EmotionAnalysisRequestDTO(Integer senderId, List<String> messages) {
    this.senderId = senderId;
    this.messages = messages;
  }
}
