package com.ar.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.app.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByUsername(String username);
	
	public Optional<User> findByEmail(String email);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.enabled = :enabled WHERE u.id =:id")
	public void updateEnabledById(@Param("id") Long id,@Param("enabled") boolean enabled);
	
	@Modifying
	@Transactional
	@Query("UPDATE User u SET "
			+ "u.password = :newPassword "
			+ "WHERE u.id = :id")
	void updatePassword(@Param("id")Long id, @Param("newPassword")String newPassword);

}
