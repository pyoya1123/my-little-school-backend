package com.project.final_project.emotionanalysis.controller;

import com.project.final_project.emotionanalysis.dto.EmotionAnalysisResponseDTO;
import com.project.final_project.emotionanalysis.service.EmotionAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emotion-analysis")
@RequiredArgsConstructor
public class EmotionAnalysisController {

  private final EmotionAnalysisService emotionAnalysisService;

  @GetMapping("/{userId}")
  public EmotionAnalysisResponseDTO getEmotionAnalysisByUserId(@PathVariable("userId") Integer userId) {
    return emotionAnalysisService.getEmotionAnalysisByUserId(userId);
  }

}
