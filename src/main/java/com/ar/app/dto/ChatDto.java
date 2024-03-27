package com.ar.app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {

	private Long id;
	private String roomId;
	private List<MessageDto> messages;
	private List<UserDto> users;
}
