package com.ar.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ar.app.dto.ChatDto;
import com.ar.app.dto.ChatRequest;
import com.ar.app.dto.UserDto;
import com.ar.app.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class ChatMessageController {
	
	private final ChatMessageService chatService;

	// =============== CHAT CONTROLLER CALLS ==================
	@PostMapping("/create")
    public ResponseEntity<ChatDto> createChat(@RequestBody ChatRequest chatRequest) {
		ChatDto createdChat = chatService.createChat(chatRequest);
        return new ResponseEntity<>(createdChat, HttpStatus.CREATED);
    }
	
	@GetMapping("/by-roomId/{roomId}")
	public ChatDto getMessagesByRoomId(@PathVariable String roomId) {
		return chatService.findByRoomId(roomId);
	}

//    @GetMapping("/chatUsers/{userId}")
//    public ResponseEntity<List<UserDto>> getOtherUsersInChat(@PathVariable Long userId) {
//        List<UserDto> otherUsers = chatService.findOtherUsersInChat(userId);
//        return ResponseEntity.ok(otherUsers);
//    }
    
    @GetMapping("/chatUsers/{userId}")
    public ResponseEntity<List<ChatDto>> getOtherUsersInChat(@PathVariable Long userId) {
        List<ChatDto> otherChatUsers = chatService.findOtherUsersChat(userId);
        return ResponseEntity.ok(otherChatUsers);
    }
    
    

}