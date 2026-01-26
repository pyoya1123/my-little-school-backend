package com.project.final_project.airecommendation.repository;

import com.project.final_project.airecommendation.domain.AIRecommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Integer> {


  @Query("SELECT r FROM AIRecommendation r WHERE r.userId = :userId "
      + "and r.recommendedUserId = :recommendedUserId")
  Optional<AIRecommendation> getRecommendationByUserIdAndRecommendedUserId(
      @Param("userId") Integer userId,
      @Param("recommendedUserId") Integer recommendedUserId);


  @Query("select r from AIRecommendation r where r.userId = :userId")
  List<AIRecommendation> findAIRecommendationListByUserId(@Param("userId") Integer userId);

  @Query("select r from AIRecommendation r where r.userId = :userId")
  List<AIRecommendation> getRecommendationByUserId(@Param("userId") Integer userId);

  @Query("select r from AIRecommendation r where r.recommendedUserId = :userId")
  List<AIRecommendation> getRecommendationByRecommendedUserId(@Param("userId") Integer userId);
}
