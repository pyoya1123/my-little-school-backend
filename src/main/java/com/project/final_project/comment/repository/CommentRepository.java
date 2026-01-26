package com.project.final_project.comment.repository;

import com.project.final_project.comment.domain.Comment;
import com.project.final_project.comment.dto.CommentResponseDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

  @Query("select c from Comment c where c.boardId = :boardId")
  List<Comment> getCommentListByBoardId(@Param("boardId") Integer boardId);

  @Query("select count(*) from Comment c where c.boardId = :boardId")
  Integer getCommentCountByBoardId(@Param("boardId") Integer boardId);
}
