package com.project.final_project.chatbotlog.repository;

import com.project.final_project.chatbotlog.domain.ChatBotLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatBotLogRepository extends JpaRepository<ChatBotLog, Integer> {

  @Query("select c from ChatBotLog c where c.userId = :userId")
  List<ChatBotLog> getChatBotLogListByUserId(@Param("userId") Integer userId);
}
