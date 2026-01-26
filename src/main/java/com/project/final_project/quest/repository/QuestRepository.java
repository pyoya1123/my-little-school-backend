package com.project.final_project.quest.repository;

import com.project.final_project.quest.domain.Quest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Integer> {

  @Query("select q from Quest q where q.questType = :questType")
  List<Quest> getQuestListByQuestType(@Param("questType") String questType);

}
