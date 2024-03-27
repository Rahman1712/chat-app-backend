package com.ar.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ar.app.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

}