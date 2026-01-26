package com.project.final_project.chatlog.domain.lastprocessedstatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "last_processed_status")
@Getter
@Setter
@NoArgsConstructor
public class LastProcessedStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "last_processed_id")
  private Integer lastProcessedId;

  public LastProcessedStatus(Integer userId, Integer lastProcessedId) {
    this.userId = userId;
    this.lastProcessedId = lastProcessedId;
  }
}
