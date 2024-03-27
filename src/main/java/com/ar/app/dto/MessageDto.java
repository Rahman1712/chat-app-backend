package com.ar.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

	private Long id;
	private Long senderId;
	private String content;
	private String contentType;
	private LocalDateTime timestamp;
}
