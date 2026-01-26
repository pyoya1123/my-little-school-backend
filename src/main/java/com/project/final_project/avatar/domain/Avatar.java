package com.project.final_project.avatar.domain;

import com.project.final_project.user.domain.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "avatar")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avatar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(name = "user_id")
  Integer userId;

  @ElementCollection
  @CollectionTable(name = "avatar_info_list", joinColumns = @JoinColumn(name = "avatar_id"))
  @Column(name = "info_list")
  List<Integer> infoList;
}
