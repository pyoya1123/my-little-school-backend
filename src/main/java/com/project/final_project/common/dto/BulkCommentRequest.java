package com.project.final_project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 댓글 일괄 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkCommentRequest {
  
  /**
   * 게시글별 생성할 댓글 수 목록
   */
  private List<BoardCommentItem> items;

  /**
   * 게시글별 댓글 생성 정보
   */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BoardCommentItem {
    /**
     * 게시글 ID
     */
    private Integer boardId;
    
    /**
     * 생성할 댓글 수
     */
    private Integer count;
  }
}
