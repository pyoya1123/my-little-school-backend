package com.project.final_project.guestbook.repository;

import com.project.final_project.guestbook.domain.GuestBook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Integer> {


  @Query("select gb from GuestBook gb where gb.mapType = :mapType and gb.mapId = :mapId")
  List<GuestBook> getGuestBookListByMapTypeAndMapId(@Param("mapType") String mapType, @Param("mapId") Integer mapId);

  @Query("select gb from GuestBook gb where gb.user.id = :userId")
  List<GuestBook> getGuestBookListByUserId(@Param("userId") Integer userId);
}
