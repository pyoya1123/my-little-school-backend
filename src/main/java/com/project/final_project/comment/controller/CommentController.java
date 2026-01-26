package com.project.final_project.comment.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.comment.domain.Comment;
import com.project.final_project.comment.dto.CommentRequestDTO;
import com.project.final_project.comment.dto.CommentResponseDTO;
import com.project.final_project.comment.dto.CommentUpdateDTO;
import com.project.final_project.comment.service.CommentService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseResult<CommentResponseDTO> insert(@RequestBody CommentRequestDTO commentRequestDTO) {
    return success(commentService.insertComment(commentRequestDTO));
  }

  @GetMapping("/{boardId}")
  public List<CommentResponseDTO> getCommentListByBoardId(@PathVariable("boardId") Integer boardId) {
    return commentService.getCommentListByBoardId(boardId);
  }

  @PatchMapping
  public ResponseResult<CommentResponseDTO> update(@RequestBody CommentUpdateDTO dto)  {
    return success(commentService.updateComment(dto));
  }

  @DeleteMapping
  public ResponseResult<String> delete(@RequestParam("commentId") Integer commentId) {
    commentService.deleteComment(commentId);

    return success("id : " + commentId + " 인 댓글이 삭제되었습니다.");
  }

}
