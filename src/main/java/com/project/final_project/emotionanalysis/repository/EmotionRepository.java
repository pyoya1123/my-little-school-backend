package com.project.final_project.emotionanalysis.repository;

import com.project.final_project.emotionanalysis.domain.EmotionAnalysis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<EmotionAnalysis, Integer> {

  @Query("select e from EmotionAnalysis e where e.userId = :userId")
  EmotionAnalysis getEmotionAnalysisByUserId(@Param("userId") Integer userId);


}
