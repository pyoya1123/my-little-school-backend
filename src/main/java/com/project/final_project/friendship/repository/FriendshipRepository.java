package com.project.final_project.friendship.repository;

import com.project.final_project.friendship.domain.Friendship;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    @Query("select f from Friendship f where f.requesterId = :requesterId and f.isAccepted = :isAccepted")
    List<Friendship> findByRequesterIdAndIsAccepted(@Param("requesterId") Integer requesterId, @Param("isAccepted") boolean isAccepted);

    @Query("select f from Friendship f where f.receiverId = :receiverId and f.isAccepted = :isAccepted")
    List<Friendship> findByReceiverIdAndIsAccepted(@Param("receiverId") Integer receiverId, @Param("isAccepted") boolean isAccepted);

    @Query("select f from Friendship f where f.requesterId = :requesterId and f.receiverId = :receiverId")
    Friendship findByRequesterIdAndReceiverId(@Param("requesterId") Integer requesterId, @Param("receiverId") Integer receiverId);

    @Query("select f from Friendship f where (f.requesterId = :userId or f.receiverId = :userId) and f.isAccepted = true ")
    List<Friendship> getAllAcceptedFriendships(@Param("userId") Integer userId);

    @Query("select f from Friendship f where (f.requesterId = :userId or f.receiverId = :userId) and f.isAccepted = false ")
    List<Friendship> getAllUnAcceptedFriendships(@Param("userId") Integer userId);
}
