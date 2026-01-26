package com.project.final_project.gallery.repository;

import com.project.final_project.gallery.domain.Gallery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {

  @Query("select g from Gallery g where g.schoolId = :schoolId")
  List<Gallery> getGalleryListBySchoolId(@Param("schoolId") Integer schoolId);

  @Query("select g from Gallery g where g.userId = :userId")
  List<Gallery> getGalleryListByUserId(@Param("userId") Integer userId);

  @Query("select g from Gallery g where g.publicId = :publicId")
  Optional<Gallery> getGalleryByPublicId(@Param("publicId") String publicId);
}
