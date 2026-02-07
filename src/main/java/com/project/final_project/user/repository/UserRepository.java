package com.project.final_project.user.repository;

import com.project.final_project.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("select u from User u where u.email = :userEmail")
  User findByUserEmail(@Param("userEmail") String userEmail);

  @Query("select u from User u where u.email = :email")
  User getUserByEmail(@Param("email") String email);

  /**
   * 모든 사용자를 조회하며 School 정보를 함께 가져옵니다 (N+1 문제 해결)
   * JOIN FETCH를 사용하여 한 번의 쿼리로 모든 데이터를 조회합니다.
   */
  @Query("select distinct u from User u left join fetch u.school")
  List<User> findAllWithSchool();

  @Query("select u from User u join fetch u.school ")
  List<User> getAllUser();
}
