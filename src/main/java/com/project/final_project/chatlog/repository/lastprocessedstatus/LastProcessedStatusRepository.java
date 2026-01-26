package com.project.final_project.chatlog.repository.lastprocessedstatus;

import com.project.final_project.chatlog.domain.lastprocessedstatus.LastProcessedStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LastProcessedStatusRepository extends JpaRepository<LastProcessedStatus, Integer> {

  @Query("select l from LastProcessedStatus l where l.userId = :userId")
  Optional<LastProcessedStatus> getLastProcessedStatusByUserId(@Param("userId") Integer userId);
}
