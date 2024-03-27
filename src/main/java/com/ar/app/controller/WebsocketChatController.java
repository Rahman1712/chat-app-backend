package com.ar.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ar.app.dto.MessageRequest;
import com.ar.app.entity.Message;
import com.ar.app.service.ChatMessageService;

@Controller
public class WebsocketChatController {
	
	@Autowired private ChatMessageService chatService;
	
	// =============== WEBSOCKET CALLS =========================
	@MessageMapping("/chat/{roomId}")
	@SendTo("/topic/chat/{roomId}")
	public Message sendChatMessage(@DestinationVariable String roomId, @Payload MessageRequest messageRequest) {
		System.err.println(roomId);
		System.err.println(messageRequest);
		Message savedMessage = chatService.saveMessage(roomId, messageRequest);
		return savedMessage;
	}
}