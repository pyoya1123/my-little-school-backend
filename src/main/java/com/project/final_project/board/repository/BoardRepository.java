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
}
