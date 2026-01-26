package com.project.final_project.boardlikemanager.repository;

import com.project.final_project.boardlikemanager.domain.BoardLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Integer> {

  @Query("select bl from BoardLike bl where bl.boardId = :boardId and bl.userId = :userId")
  BoardLike getBoardLikeByBoardIdAndUserId(
      @Param("boardId") Integer boardId,
      @Param("userId") Integer userId
  );

  @Query("select bl from BoardLike bl where bl.userId = :userId")
  List<BoardLike> getBoardLikeByUserId(@Param("userId") Integer userId);
}
