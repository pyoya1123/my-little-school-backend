package com.project.final_project.guestbook.controller;

import com.project.final_project.guestbook.dto.GuestBookDTO;
import com.project.final_project.guestbook.dto.GuestBookRegisterDTO;
import com.project.final_project.guestbook.service.GuestBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guest-book")
@RequiredArgsConstructor
public class GuestBookController {

  private final GuestBookService guestBookService;

  @PostMapping
  public GuestBookDTO registerGuestBook(@RequestBody GuestBookRegisterDTO dto){
    return guestBookService.registerGuestBook(dto);
  }

  @GetMapping("/list/{mapType}/{mapId}")
  public List<GuestBookDTO> getGuestBookListByMapTypeAndMapId(@PathVariable("mapType") String mapType, @PathVariable("mapId") Integer mapId) {
    return guestBookService.getGuestBookListByMapTypeAndMapId(mapType, mapId);
  }

  @DeleteMapping
  public ResponseEntity<?> removeGuestBookById(@RequestParam("guestBookId") Integer guestBookId){
    guestBookService.removeGuestBookById(guestBookId);
    return ResponseEntity.noContent().build();
  }

}
