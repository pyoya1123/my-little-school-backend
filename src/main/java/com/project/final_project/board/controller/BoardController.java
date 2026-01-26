package com.project.final_project.board.controller;

import com.project.final_project.board.dto.BoardListResponseDTO;
import com.project.final_project.boardlikemanager.dto.BoardAddLikeDTO;
import com.project.final_project.board.dto.BoardDTO;
import com.project.final_project.board.dto.BoardRegisterDTO;
import com.project.final_project.board.dto.BoardUpdateDTO;
import com.project.final_project.board.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  @PostMapping
  public BoardDTO registerBoard(@RequestBody BoardRegisterDTO dto){
    return boardService.registerBoard(dto);
  }

  @GetMapping("/all-list/{userId}")
  public List<BoardListResponseDTO> getAllBoards(@PathVariable("userId") Integer userId) {
    return boardService.getAllBoards(userId);
  }

  @GetMapping("/list/{userId}")
  public List<BoardListResponseDTO> getBoardListByUserId(@PathVariable("userId") Integer userId) {
    return boardService.getBoardListByUserId(userId);
  }

  @GetMapping("/{boardId}")
  public BoardDTO getBoardByBoardId(@PathVariable("boardId") Integer boardId) {
    return boardService.getBoardByBoardId(boardId);
  }

  @PatchMapping
  public BoardDTO updateBoard(@RequestBody BoardUpdateDTO dto){
    return boardService.updateBoard(dto);
  }

  @DeleteMapping("/{boardId}")
  public ResponseEntity<?> deleteBoard(@PathVariable("boardId") Integer boardId) {
    boardService.deleteBoard(boardId);
    return ResponseEntity.noContent().build();
  }
}
