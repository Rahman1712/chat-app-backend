package com.ar.app.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "sender_id")
	private Long senderId;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content",length=100000)
	private String content;
	
	@Column(name = "content_type")
	private String contentType;
	
	@Column(name = "timestamp")
	private LocalDateTime timestamp;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "chat_id")
	private Chat chat;
}


