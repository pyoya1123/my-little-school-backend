package com.project.final_project.quest.repository;

import com.project.final_project.quest.domain.QuestReward;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestRewardRepository extends JpaRepository<QuestReward, Integer> {

  @Query("select q from QuestReward q where q.quest.id = :questId")
  List<QuestReward> getQuestRewardListByQuestId(@Param("questId") Integer questId);
}
