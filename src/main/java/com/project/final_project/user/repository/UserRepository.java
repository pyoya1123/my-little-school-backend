package com.project.final_project.user.repository;

import com.project.final_project.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("select u from User u where u.email = :userEmail")
  User findByUserEmail(@Param("userEmail") String userEmail);

  @Query("select u from User u where u.email = :email")
  User getUserByEmail(@Param("email") String email);
}
