package com.ar.app.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.app.dto.AuthenticationRequest;
import com.ar.app.dto.AuthenticationResponse;
import com.ar.app.dto.UserRegisterRequest;
import com.ar.app.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthenticationController {

	private final UserService userService;
	
	@GetMapping("/work")
	public String work() {
		return "Worked";
	}

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserRegisterRequest request) {
		System.err.println(request);
		return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED); 
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authRequest, HttpServletResponse response) {
		return ResponseEntity.ok(userService.authenticate(authRequest, response));
	}
	
	@PostMapping("/verify-otp") // For OTP verify at signup
	public ResponseEntity<AuthenticationResponse> verifyOtp(@RequestParam String email, @RequestParam String otp,HttpServletResponse response) {
		return ResponseEntity.ok(userService.verifyOtp(email, otp, response));
	}
	
	@PostMapping("/verify-forgot-otp") // For OTP verify at forgot time : return token
	public ResponseEntity<?> verifyForgotOtp(@RequestParam String email, @RequestParam String otp) {
		return ResponseEntity.ok(userService.verifyForgotOtp(email, otp));
	} 
	
	@PostMapping("/mail-reset-otp") // For OTP at forgot time , (also that function use in register)
	public ResponseEntity<String> verifyOtp(@RequestParam String email) throws UnsupportedEncodingException, MessagingException {
		return ResponseEntity.ok(userService.sendMailForVerify(email));
	}
	
	@PutMapping("/resend-otp") // For OTP at signup, forgot time
	public ResponseEntity<String> resendOtp(@RequestParam String email) {
		return ResponseEntity.ok(userService.refreshOtp(email)); 
	}
	
	@PutMapping("/update-password") // For Password update at forgot time 
	public ResponseEntity<String> resendOtp(@RequestParam String username, String newPassword, String token) {
		return ResponseEntity.ok(userService.updateForgotPassword(username, newPassword, token)); 
	}

	
}