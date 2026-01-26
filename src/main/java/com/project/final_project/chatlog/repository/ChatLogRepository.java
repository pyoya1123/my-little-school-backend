package com.project.final_project.chatlog.repository;

import com.project.final_project.chatlog.domain.ChatLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Integer> {

  @Query("select c from ChatLog c where c.senderId = :senderId")
  List<ChatLog> getChatLogsBySenderId(@Param("senderId") Integer senderId);

}
