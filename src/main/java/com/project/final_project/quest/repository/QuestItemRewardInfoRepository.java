package com.project.final_project.quest.repository;

import com.project.final_project.quest.domain.QuestItemRewardInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestItemRewardInfoRepository extends JpaRepository<QuestItemRewardInfo, Integer> {

  @Query("select q from QuestItemRewardInfo q where q.questId = :questId")
  List<QuestItemRewardInfo> getQuestItemRewardInfoListByQuestId(@Param("questId") Integer questId);
}
