package com.ar.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

	private String roomId;
	private Long chatId;
	private Long senderId;
	private String content;
	private String contentType;
	private LocalDateTime timestamp;
}