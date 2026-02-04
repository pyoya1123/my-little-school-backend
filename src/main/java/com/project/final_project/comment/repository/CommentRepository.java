package com.project.final_project.comment.repository;

import com.project.final_project.comment.domain.Comment;
import com.project.final_project.comment.dto.CommentResponseDTO;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

  @Query("select c from Comment c where c.boardId = :boardId")
  List<Comment> getCommentListByBoardId(@Param("boardId") Integer boardId);

  @Query("select count(*) from Comment c where c.boardId = :boardId")
  Long getCommentCountByBoardId(@Param("boardId") Integer boardId);

  /**
   * 여러 게시글의 댓글 수를 한 번에 조회
   * @param boardIds 게시글 ID 목록
   * @return boardId를 key, 댓글 수를 value로 하는 Map
   */
  @Query("select c.boardId as boardId, count(c) as commentCount " +
         "from Comment c " +
         "where c.boardId in :boardIds " +
         "group by c.boardId")
  List<Object[]> getCommentCountsByBoardIds(@Param("boardIds") List<Integer> boardIds);
}
