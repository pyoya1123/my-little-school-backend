package com.project.final_project.airecommendation.controller;

import com.project.final_project.airecommendation.domain.AIRecommendation;
import com.project.final_project.airecommendation.dto.RecommendResponseDTO;
import com.project.final_project.airecommendation.repository.AIRecommendationRepository;
import com.project.final_project.airecommendation.service.AIRecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai-recommend")
@RequiredArgsConstructor
public class AIRecommendationController {

  private final AIRecommendationService aiRecommendationService;

  @GetMapping("/list/recommendation-info")
  public List<RecommendResponseDTO> getRecommendationInfoList(@RequestParam("userId") Integer userId) {
    return aiRecommendationService.getRecommendationInfoList(userId);
  }

  @DeleteMapping("/{recommendationInfoId}")
  public ResponseEntity<?> deleteRecommendationInfo(@PathVariable("recommendationInfoId") Integer recommendationInfoId){
    aiRecommendationService.deleteRecommendationInfo(recommendationInfoId);
    return ResponseEntity.noContent().build();
  }

}

