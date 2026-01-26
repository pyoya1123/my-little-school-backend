package com.project.final_project.emotionanalysis.dto;

import com.project.final_project.emotionanalysis.domain.EmotionAnalysis;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmotionAnalysisResponseDTO {
    private String message;
    private String emotion;
    private Double score;

    public EmotionAnalysisResponseDTO(EmotionAnalysis emotionAnalysis) {
        if(emotionAnalysis != null) {
            this.message = emotionAnalysis.getMessage();
            this.emotion = emotionAnalysis.getEmotion();
            this.score = emotionAnalysis.getScore();
        }
    }
}
