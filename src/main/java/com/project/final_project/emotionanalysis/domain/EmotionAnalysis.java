package com.project.final_project.emotionanalysis.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emotion_analysis")
@Getter
@Setter
@NoArgsConstructor
public class EmotionAnalysis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "message")
  private String message;

  @Column(name = "emotion")
  private String emotion;

  @Column(name = "score")
  private Double score;

  @Column(name = "user_id")
  private Integer userId;

  public EmotionAnalysis(String message, String emotion, Double score, Integer userId) {
    this.message = message;
    this.emotion = emotion;
    this.score = score;
    this.userId = userId;
  }
}
