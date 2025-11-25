package com.nguyendat.linkedin.repository;

import com.nguyendat.linkedin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    // Get user with all relationships loaded
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.experiences " +
           "LEFT JOIN FETCH u.educations " +
           "LEFT JOIN FETCH u.userSkills us " +
           "LEFT JOIN FETCH us.skill " +
           "WHERE u.id = :userId")
    Optional<User> findByIdWithDetails(@Param("userId") Long userId);
    
    // Count connections (accepted only)
    @Query("SELECT COUNT(c) FROM Connection c WHERE " +
           "(c.requester.id = :userId OR c.receiver.id = :userId) " +
           "AND c.status = 'ACCEPTED'")
    Integer countConnections(@Param("userId") Long userId);
    
    // Count followers
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following.id = :userId")
    Integer countFollowers(@Param("userId") Long userId);
    
    // Count following
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    Integer countFollowing(@Param("userId") Long userId);
    
    // Check if users are connected
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM Connection c WHERE " +
           "((c.requester.id = :userId1 AND c.receiver.id = :userId2) OR " +
           " (c.requester.id = :userId2 AND c.receiver.id = :userId1)) " +
           "AND c.status = 'ACCEPTED'")
    Boolean areConnected(@Param("userId1") Long userId1, 
                        @Param("userId2") Long userId2);
    
    // Get connection status between two users
    @Query("SELECT c.status FROM Connection c WHERE " +
           "((c.requester.id = :userId1 AND c.receiver.id = :userId2) OR " +
           " (c.requester.id = :userId2 AND c.receiver.id = :userId1))")
    Optional<String> getConnectionStatus(@Param("userId1") Long userId1, 
                                         @Param("userId2") Long userId2);
    
    // Check if user1 is following user2
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM Follow f WHERE f.follower.id = :followerId " +
           "AND f.following.id = :followingId")
    Boolean isFollowing(@Param("followerId") Long followerId,
                       @Param("followingId") Long followingId);
}