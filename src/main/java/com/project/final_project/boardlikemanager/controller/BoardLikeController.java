package com.project.final_project.boardlikemanager.controller;

import com.project.final_project.boardlikemanager.dto.BoardAddLikeDTO;
import com.project.final_project.boardlikemanager.dto.BoardGetLikeDTO;
import com.project.final_project.boardlikemanager.dto.BoardLikeDTO;
import com.project.final_project.boardlikemanager.dto.BoardRemoveLikeDTO;
import com.project.final_project.boardlikemanager.service.BoardLikeService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board-like")
public class BoardLikeController {

  private final BoardLikeService boardLikeService;

  @PostMapping("/add-like")
  public ResponseEntity<?> addLike(@RequestBody BoardAddLikeDTO dto) {
    boardLikeService.addLike(dto);
    return ResponseEntity.ok(Map.of("message", "좋아요가 추가되었습니다."));
  }


  @GetMapping("/{boardId}/{userId}")
  public Boolean isExistLike(
      @PathVariable("boardId") Integer boardId,
      @PathVariable("userId") Integer userId) {
    return boardLikeService.isExistLike(new BoardGetLikeDTO(boardId, userId));
  }

  @DeleteMapping
  public ResponseEntity<?> removeLike(@RequestBody BoardRemoveLikeDTO dto) {
    boardLikeService.removeLIke(dto);
    return ResponseEntity.noContent().build();
  }
}
