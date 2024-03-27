package com.ar.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.app.entity.Chat;
import com.ar.app.entity.User;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

	Optional<Chat> findByRoomId(String roomId);

	@Query("SELECT c FROM Chat c WHERE :userId MEMBER OF c.users")
	List<Chat> findByUserIdInChat(@Param("userId") Long userId);

	List<Chat> findByUsersId(Long userId);
	
	@Query("SELECT c FROM Chat c WHERE :user MEMBER OF c.users")
	List<Chat> findChatsByUser(@Param("user") User user);

	@Query("SELECT DISTINCT c.users FROM Chat c WHERE :user MEMBER OF c.users")
	List<User> findChatGroupsByUser(@Param("user") User user);
}
