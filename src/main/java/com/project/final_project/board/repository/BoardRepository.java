package com.project.final_project.board.repository;

import com.project.final_project.board.domain.Board;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

  @Query("select b from Board b where b.userId = :userId")
  List<Board> getBoardListByUserId(@Param("userId") Integer userId);

  @Query("SELECT b, count(c), bl" +
          " FROM Board b" +
          " LEFT JOIN Comment c ON c.boardId = b.id " +
          " LEFT JOIN BoardLike bl ON bl.boardId = b.id AND bl.userId = :userId " +
          " WHERE b.userId = :userId" +
          " GROUP BY b, bl")
  List<Object[]> getBoardListWithCommentAndBoardLikeByUserId(
      @Param("userId") Integer userId
  );
}
