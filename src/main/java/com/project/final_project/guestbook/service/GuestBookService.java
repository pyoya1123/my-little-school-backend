package com.project.final_project.guestbook.service;

import com.project.final_project.guestbook.domain.GuestBook;
import com.project.final_project.guestbook.dto.GuestBookDTO;
import com.project.final_project.guestbook.dto.GuestBookRegisterDTO;
import com.project.final_project.guestbook.repository.GuestBookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestBookService {

  private final GuestBookRepository guestBookRepository;

  public GuestBookDTO registerGuestBook(GuestBookRegisterDTO dto) {
    GuestBook guestBook = GuestBook.createGuestBook(dto);
    return new GuestBookDTO(guestBookRepository.save(guestBook));
  }

  public void removeGuestBookById(Integer guestBookId) {
    guestBookRepository.deleteById(guestBookId);
  }

  public List<GuestBookDTO> getGuestBookListByMapTypeAndMapId(String mapType, Integer mapId) {
    return guestBookRepository.getGuestBookListByMapTypeAndMapId(mapType, mapId).stream().map(GuestBookDTO::new).toList();
  }

  @Transactional
  public void deleteGuestBookListByUserId(Integer userId) {
    guestBookRepository.deleteAll(guestBookRepository.getGuestBookListByUserId(userId));
  }
}
