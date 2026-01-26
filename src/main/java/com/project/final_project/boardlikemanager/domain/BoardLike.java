package com.project.final_project.boardlikemanager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board_like")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "board_id")
  private Integer boardId;

  @Column(name = "user_id")
  private Integer userId;

  public BoardLike(Integer boardId, Integer userId) {
    this.boardId = boardId;
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "BoardLike{" +
        "id=" + id +
        ", boardId=" + boardId +
        ", userId=" + userId +
        '}';
  }
}
