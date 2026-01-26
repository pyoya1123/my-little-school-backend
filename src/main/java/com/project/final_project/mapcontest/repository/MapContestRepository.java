package com.project.final_project.mapcontest.repository;

import com.project.final_project.mapcontest.domain.MapContest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MapContestRepository extends JpaRepository<MapContest, Integer> {

  @Query("select m from MapContest m where m.id = :mapContestId")
  MapContest getMapContestByMapContestId(@Param("mapContestId") Integer mapContestId);

  @Query("select m from MapContest m where m.userId = :userId")
  List<MapContest> getMapContestListByUserId(@Param("userId") Integer userId);
}
