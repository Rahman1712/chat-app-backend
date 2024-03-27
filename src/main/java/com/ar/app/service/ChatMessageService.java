package com.ar.app.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ar.app.dto.ChatDto;
import com.ar.app.dto.ChatRequest;
import com.ar.app.dto.MessageRequest;
import com.ar.app.dto.UserDto;
import com.ar.app.entity.Chat;
import com.ar.app.entity.Message;
import com.ar.app.entity.User;
import com.ar.app.exception.ChatException;
import com.ar.app.exception.UserException;
import com.ar.app.repo.ChatRepository;
import com.ar.app.repo.MessageRepository;
import com.ar.app.repo.UserRepository;
import com.ar.app.utils.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	
	public ChatDto createChat(ChatRequest chatRequest) {
		Optional<Chat> chatOptional = chatRepository.findByRoomId(chatRequest.getRoomId());
		
		if(chatOptional.isPresent()) {
			return chatOptional.map(UserUtils::chatToChatDto).get();
		}
		
		Chat chat = new Chat();
		User user1 = userRepository.findById(chatRequest.getUser1Id()).orElseThrow(() -> new UserException("Not Valid User"));
		User user2 = userRepository.findById(chatRequest.getUser2Id()).orElseThrow(() -> new UserException("Not Valid User"));
		chat.setUsers(Arrays.asList(user1, user2));
		
		chat.setRoomId(chatRequest.getRoomId());
		
		return Optional.of(chatRepository.save(chat))
				.map(UserUtils::chatToChatDto).get();
		 
	}
	
	public void deleteById(Long chatId) {
		chatRepository.deleteById(chatId);
	}
	
	public ChatDto findChatById(Long chatId){
		Optional<Chat> chat = chatRepository.findById(chatId);
		if(chat.isPresent()) {
			return chat.map(UserUtils::chatToChatDto).get();
		}
		throw new ChatException("Chat not found");
	}
	
	public ChatDto findByRoomId(String roomId){
		return chatRepository.findByRoomId(roomId)
				.map(UserUtils::chatToChatDto).orElse(null);
	}

	public List<ChatDto> findChatsByUserId(Long userId){
		List<Chat> chats = chatRepository.findByUserIdInChat(userId);
		return chats.stream().map(UserUtils::chatToChatDto).toList();
	}
	
	public List<UserDto> findOtherUsersInChat(Long userId){
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException("Not Valid User"));
		
		List<UserDto> list = chatRepository.findChatGroupsByUser(user)
		.stream().map(UserUtils::userToUserDto)
		.filter(u -> u.getId() != userId)
		.toList();
		System.err.println(list);
		
		return list;
	}
	
	public List<ChatDto> findOtherUsersChat(Long userId){
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException("Not Valid User"));
		
		List<ChatDto> list = chatRepository.findChatsByUser(user)
		.stream().map(UserUtils::chatToChatDtoMini)
		.filter(u -> u.getId() != userId)
		.toList();
		System.err.println(list);
		
		return list;
	}
	
	public Message saveMessage(String roomId, MessageRequest messageRequest) {
		Chat chat = chatRepository.findByRoomId(roomId)
				.orElseThrow(() ->  new ChatException("Chat not fount with room id "+ roomId));
		
		Message message = Message.builder()
				.chat(chat)
				.content(messageRequest.getContent())
				.contentType(messageRequest.getContentType())
				.timestamp(LocalDateTime.now())
				.senderId(messageRequest.getSenderId())
				.build();
		
		return messageRepository.save(message);
	}
	
}
