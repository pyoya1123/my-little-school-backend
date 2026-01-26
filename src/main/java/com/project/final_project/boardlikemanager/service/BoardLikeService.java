package com.project.final_project.boardlikemanager.service;

import com.project.final_project.board.domain.Board;
import com.project.final_project.board.repository.BoardRepository;
import com.project.final_project.boardlikemanager.dto.BoardAddLikeDTO;
import com.project.final_project.boardlikemanager.domain.BoardLike;
import com.project.final_project.boardlikemanager.dto.BoardGetLikeDTO;
import com.project.final_project.boardlikemanager.dto.BoardRemoveLikeDTO;
import com.project.final_project.boardlikemanager.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

  private final BoardLikeRepository boardLikeRepository;
  private final BoardRepository boardRepository;

  public void addLike(BoardAddLikeDTO dto){
    BoardLike bl = boardLikeRepository.getBoardLikeByBoardIdAndUserId(dto.getBoardId(), dto.getUserId());

    if(bl != null) {
      throw new IllegalArgumentException("이미 좋아요를 누르셨습니다.");
    }

    Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
        () -> new IllegalArgumentException("not found board id : " + dto.getBoardId()));
    board.setLikeCount(board.getLikeCount() + 1);

    bl = new BoardLike(dto.getBoardId(), dto.getUserId());

    boardLikeRepository.save(bl);
  }

  public Boolean isExistLike(BoardGetLikeDTO dto){
    return boardLikeRepository.getBoardLikeByBoardIdAndUserId(
        dto.getBoardId(), dto.getUserId()) != null;
  }

  public void removeLIke(BoardRemoveLikeDTO dto){

    System.out.println(dto);

    BoardLike bl = boardLikeRepository.getBoardLikeByBoardIdAndUserId(
        dto.getBoardId(), dto.getUserId());
    if(bl == null) {
      throw new IllegalArgumentException("좋아요를 누르지 않았습니다.");
    }

    System.out.println(bl);

    Board board = boardRepository.findById(dto.getBoardId()).orElseThrow(
        () -> new IllegalArgumentException("not found board id : " + dto.getBoardId()));
    board.setLikeCount(board.getLikeCount() - 1);

    boardLikeRepository.delete(bl);
  }

  @Transactional
  public void deleteBoardLikeListByUserId(Integer userId) {
    boardLikeRepository.deleteAll(boardLikeRepository.getBoardLikeByUserId(userId));
  }
}
