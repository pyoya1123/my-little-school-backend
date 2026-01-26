package com.project.final_project.board.domain;

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

@Getter
@Setter
@Entity
@Table(name = "board")
@AllArgsConstructor
@NoArgsConstructor
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "board_title")
  String title;

  @Column(name = "board_content")
  String content;

  @Column(name = "board_like_count")
  Integer likeCount = 0;

  @Column(name = "user_id")
  Integer userId;

  public Board(String title, String content, Integer userId) {
    this.title = title;
    this.content = content;
    this.likeCount = 0;
    this.userId = userId;
  }
}
