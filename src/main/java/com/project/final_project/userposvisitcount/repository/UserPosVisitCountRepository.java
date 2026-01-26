package com.project.final_project.userposvisitcount.repository;

import com.project.final_project.userposvisitcount.domain.UserPosVisitCount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface UserPosVisitCountRepository extends JpaRepository<UserPosVisitCount, Integer> {

  @Query("select u from UserPosVisitCount u where u.mapType = :mapType and u.userId = :userId")
  UserPosVisitCount getUserPosVisitCountByMapIdAndMapTypeAndUserId(
      @Param("mapType") String mapType,
      @Param("userId") int userId);

  @Query("select upvc from UserPosVisitCount upvc where upvc.userId = :userId")
  List<UserPosVisitCount> getUserPosVisitCountListByUserId(@Param("userId") Integer userId);
}
