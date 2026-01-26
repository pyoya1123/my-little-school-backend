package com.project.final_project.quest.repository;

import com.project.final_project.quest.domain.UserQuest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserQuestRepository extends JpaRepository<UserQuest, Integer> {
  @Query("select q from UserQuest q where q.quest.id = :questId and q.user.id = :userId")
  UserQuest getUserQuestByQuestIdAndUserId(
      @Param("questId") Integer questId,
      @Param("userId") Integer userId
  );

  @Query("select q from UserQuest q where q.user.id = :userId and q.isComplete = true")
  List<UserQuest> getCompletedUserQuestListByUserId(@Param("userId") Integer userId);

  @Query("select q from UserQuest q where q.user.id = :userId and q.isComplete = false")
  List<UserQuest> getNotCompletedUserQuestListByUserId(@Param("userId") Integer userId);

  @Query("select q from UserQuest q where q.user.id = :userId")
  List<UserQuest> getUserQuestListByUserId(@Param("userId") Integer userId);

  @Query("select q from UserQuest q where q.id = :questId")
  UserQuest getUserQuestByQuestId(@Param("questId") Integer questId);
}
