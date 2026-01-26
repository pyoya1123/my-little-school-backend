package com.project.final_project.note.controller;

import com.project.final_project.note.dto.NoteDTO;
import com.project.final_project.note.dto.NoteRegisterDTO;
import com.project.final_project.note.service.NoteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

  private final NoteService noteService;

  public NoteDTO registerNote(NoteRegisterDTO dto) {
    return noteService.registerNote(dto);
  }

  public List<NoteDTO> getAllNote() {
    return noteService.getAllNote();
  }

  public ResponseEntity<?> removeNote(@RequestParam("noteId") Integer noteId) {
    noteService.removeNote(noteId);
    return ResponseEntity.noContent().build();
  }
}
