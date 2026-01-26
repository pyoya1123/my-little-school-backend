package com.project.final_project.note.repository;

import com.project.final_project.note.domain.Note;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

  @Query("select n from Note n where n.fromUserId = :userId")
  List<Note> getNoteListByUserId(@Param("userId") Integer userId);
}
