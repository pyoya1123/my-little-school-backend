package com.project.final_project.avatar.repository;

import com.project.final_project.avatar.domain.Avatar;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

  @Query("select a from Avatar a where a.userId = :userId")
  Optional<Avatar> getAvatarByUserId(@Param("userId") Integer userId);

}
