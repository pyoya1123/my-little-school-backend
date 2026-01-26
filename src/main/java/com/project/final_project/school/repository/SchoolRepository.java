package com.project.final_project.school.repository;

import com.project.final_project.school.domain.School;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {

  List<School> findBySchoolNameContaining(String schoolName);

  @Query(value = "SELECT s2.* FROM school s1 " +
      "JOIN school s2 ON s1.id != s2.id " +
      "WHERE s1.school_name = :schoolName " +
      "AND (6371 * ACOS(COS(RADIANS(s1.latitude)) * COS(RADIANS(s2.latitude)) * " +
      "COS(RADIANS(s2.longitude) - RADIANS(s1.longitude)) + " +
      "SIN(RADIANS(s1.latitude)) * SIN(RADIANS(s2.latitude)))) <= :radius",
      nativeQuery = true)
  List<School> getSchoolListWithinRadiusBySchoolName(@Param("schoolName") String schoolName,
      @Param("radius") double radius);
}
