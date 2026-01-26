package com.project.final_project.note.service;

import com.project.final_project.note.domain.Note;
import com.project.final_project.note.dto.NoteDTO;
import com.project.final_project.note.dto.NoteRegisterDTO;
import com.project.final_project.note.repository.NoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteDTO registerNote(NoteRegisterDTO dto) {
    Note note = Note.createNote(dto);
    return new NoteDTO(noteRepository.save(note));
  }

  public List<NoteDTO> getAllNote() {
    return noteRepository.findAll().stream().map(NoteDTO::new).toList();
  }

  public void removeNote(Integer noteId) {
    noteRepository.deleteById(noteId);
  }

  @Transactional
  public void deleteNoteListByUserId(Integer userId) {
    noteRepository.deleteAll(noteRepository.getNoteListByUserId(userId));
  }
}
