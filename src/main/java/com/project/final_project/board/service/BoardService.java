package com.project.final_project.board.service;

import com.project.final_project.board.domain.Board;
import com.project.final_project.board.dto.BoardDTO;
import com.project.final_project.board.dto.BoardListResponseDTO;
import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.dto.BoardUpdateDTO;
import com.project.final_project.board.repository.BoardRepository;
import com.project.final_project.boardlikemanager.dto.BoardGetLikeDTO;
import com.project.final_project.boardlikemanager.service.BoardLikeService;
import com.project.final_project.comment.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final CommentService commentService;
  private final BoardLikeService boardLikeService;

  public BoardDTO registerBoard(BoardRegisterDTO dto) {
    Board board = new Board(dto.getTitle(), dto.getContent(), dto.getUserId());
    return new BoardDTO(boardRepository.save(board), 0);
  }


  public List<BoardListResponseDTO> getBoardListByUserId(Integer userId) {
    return boardRepository.getBoardListByUserId(userId)
        .stream().map(b -> new BoardListResponseDTO(
            b,
            commentService.getCommentCountByBoardId(b.getId()),
            boardLikeService.isExistLike(new BoardGetLikeDTO(b.getId(), userId))
            )
        )
        .toList();
  }


  public BoardDTO getBoardByBoardId(Integer boardId) {
    Board board = boardRepository.findById(boardId).orElseThrow(
        () -> new IllegalStateException("not found board id : " + boardId));
    return new BoardDTO(board, commentService.getCommentCountByBoardId(board.getId()));
  }


  @Transactional
  public BoardDTO updateBoard(BoardUpdateDTO dto) {
    Board foundBoard = boardRepository.findById(dto.getBoardId()).orElseThrow(
        () -> new IllegalStateException("not found board id : " + dto.getBoardId()));

    if(dto.getContent() != null) {
      foundBoard.setContent(dto.getContent());
    }

    if(dto.getTitle() != null) {
      foundBoard.setTitle(dto.getTitle());
    }

    if(dto.getLikeCount() != null){
      foundBoard.setLikeCount(dto.getLikeCount());
    }

    return new BoardDTO(foundBoard, commentService.getCommentCountByBoardId(foundBoard.getId()));
  }


  @Transactional
  public void deleteBoard(Integer boardId) {
    boardRepository.deleteById(boardId);
  }


  @Transactional
  public void deleteBoardListByUserId(Integer userId) {
    List<Board> boardList = boardRepository.getBoardListByUserId(userId);
    boardList.forEach(b -> {
      commentService.deleteCommentListByBoardId(b.getId());
      boardRepository.delete(b);
    });
  }


  public List<BoardListResponseDTO> getAllBoards(Integer userId) {
    return boardRepository.findAll()
        .stream().map(b -> new BoardListResponseDTO(
                b,
                commentService.getCommentCountByBoardId(b.getId()),
                boardLikeService.isExistLike(new BoardGetLikeDTO(b.getId(), userId))
            )
        )
        .toList();
  }
}
