package com.ar.app.utils;

import java.util.List;

import com.ar.app.dto.ChatDto;
import com.ar.app.dto.MessageDto;
import com.ar.app.dto.UserDto;
import com.ar.app.entity.Chat;
import com.ar.app.entity.Message;
import com.ar.app.entity.User;

public class UserUtils {

	public static UserDto userToUserDto(User user) {
		return UserDto.builder()
				.id(user.getId())
				.username(user.getUsername())
				.fullname(user.getFullname())
				.email(user.getEmail())
				.mobile(user.getMobile())
				.build();
	}
	
	public static ChatDto chatToChatDtoMini(Chat chat) {
		List<UserDto> userDtosList = chat.getUsers() != null ?
	             chat.getUsers()
				.stream()
				.map(UserUtils::userToUserDto)
				.toList() 
				: null;
		
		return ChatDto.builder()
				.id(chat.getId())
				.roomId(chat.getRoomId())
				.users(userDtosList)
				.build();
	}
	
	public static ChatDto chatToChatDto(Chat chat) {
		List<UserDto> userDtosList = chat.getUsers() != null ?
	             chat.getUsers()
				.stream()
				.map(UserUtils::userToUserDto)
				.toList() 
				: null;
		
		List<MessageDto> messageDtosList = chat.getMessages() != null ?
				chat.getMessages()
				.stream()
				.map(UserUtils::messageToMessageDto)
				.toList() 
				: null;
		
		return ChatDto.builder()
				.id(chat.getId())
				.roomId(chat.getRoomId())
				.users(userDtosList)
				.messages(messageDtosList)
				.build();
	}
	
	public static MessageDto messageToMessageDto(Message message) {
		return MessageDto.builder()
				.id(message.getId())
				.senderId(message.getSenderId())
				.content(message.getContent())
				.contentType(message.getContentType())
				.timestamp(message.getTimestamp())
				.build();
	}
}