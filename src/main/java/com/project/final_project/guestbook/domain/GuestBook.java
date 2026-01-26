package com.project.final_project.guestbook.domain;

import com.project.final_project.guestbook.dto.GuestBookRegisterDTO;
import com.project.final_project.user.domain.User;
import com.project.final_project.user.dto.UserDTO;
import com.project.final_project.user.dto.UserGuestBookDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guest_book")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestBook {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "content")
  private String content; // 쪽지 내용

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user; // 쪽지 남긴 유저

  @Column(name = "map_id")
  private Integer mapId;

  @Column(name = "map_type")
  private String mapType;

  @Column(name = "background_color")
  private Integer backgroundColor;


  public static GuestBook createGuestBook(GuestBookRegisterDTO dto){
    GuestBook guestBook = new GuestBook();
    guestBook.setContent(dto.getContent());
    guestBook.setMapType(dto.getMapType());
    guestBook.setBackgroundColor(dto.getBackgroundColor());
    guestBook.setMapId(dto.getMapId());
    guestBook.setUser(dto.getUser());
    return guestBook;
  }
}
