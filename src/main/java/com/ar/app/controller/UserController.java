package com.ar.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ar.app.dto.UserDto;
import com.ar.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;

	@GetMapping("/all")
	public ResponseEntity<List<UserDto>> findAllUsers() {
		List<UserDto> otherUsers = userService.findAllUsers();
		return ResponseEntity.ok(otherUsers);
	}
}
