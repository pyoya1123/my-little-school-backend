package com.project.final_project.comment.service;

import com.project.final_project.comment.domain.Comment;
import com.project.final_project.comment.dto.CommentRequestDTO;
import com.project.final_project.comment.dto.CommentResponseDTO;
import com.project.final_project.comment.dto.CommentUpdateDTO;
import com.project.final_project.comment.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  public CommentResponseDTO insertComment(CommentRequestDTO commentRequestDTO) {
    Comment comment = new Comment(commentRequestDTO.getContent(), commentRequestDTO.getBoardId());
    return new CommentResponseDTO(commentRepository.save(comment));
  }

  public List<CommentResponseDTO> getCommentListByBoardId(Integer boardId) {
    return commentRepository.getCommentListByBoardId(boardId)
        .stream().map(CommentResponseDTO::new).toList();
  }

  @Transactional
  public CommentResponseDTO updateComment(CommentUpdateDTO dto) {
    Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(
        () -> new IllegalStateException("not found comment id : " + dto.getCommentId()));

    if(dto.getContent() != null) {
      comment.setContent(dto.getContent());
    }

    return new CommentResponseDTO(comment);
  }

  @Transactional
  public void deleteComment(Integer commentId) {
    commentRepository.deleteById(commentId);
  }

  public Integer getCommentCountByBoardId(Integer boardId) {
    return commentRepository.getCommentCountByBoardId(boardId);
  }

  @Transactional
  public void deleteCommentListByBoardId(Integer boardId) {
    commentRepository.deleteAll(commentRepository.getCommentListByBoardId(boardId));
  }
}
