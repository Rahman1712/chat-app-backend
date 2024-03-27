package com.ar.app.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ar.app.config.JwtService;
import com.ar.app.dto.AuthenticationRequest;
import com.ar.app.dto.AuthenticationResponse;
import com.ar.app.dto.UserDto;
import com.ar.app.dto.UserRegisterRequest;
import com.ar.app.entity.User;
import com.ar.app.exception.ErrorResponse;
import com.ar.app.exception.UserException;
import com.ar.app.exception.UserRegisterException;
import com.ar.app.repo.UserRepository;
import com.ar.app.utils.UserUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final OTPService otpService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public UserDto findById(Long id) {
		return userRepository.findById(id).map(UserUtils::userToUserDto).orElse(null);
	}

	public UserDto findByUsername(String username) {
		return userRepository.findByUsername(username).map(UserUtils::userToUserDto).orElse(null);
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public UserDto findByEmail(String email) {
		return userRepository.findByEmail(email).map(UserUtils::userToUserDto).orElse(null);
	}

	public void updateEnabledById(Long id, boolean enabled) {
		userRepository.updateEnabledById(id, enabled);
	}

	// register
	public AuthenticationResponse register(UserRegisterRequest request) {
		var user = User.builder().fullname(request.getFullname()).mobile(request.getMobile()).email(request.getEmail())
				.username(request.getUsername()).password(passwordEncoder.encode(request.getPassword())).nonLocked(true)
				.enabled(false).build();

		userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
			throw new UserRegisterException(
					new ErrorResponse("username already exists.", "username", request.getUsername()));
		});
		userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
			throw new UserRegisterException(new ErrorResponse("email already exists.", "email", request.getEmail()));
		});

		User userSaved = userRepository.save(user);

		try {
			sendMailForVerify(userSaved);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
			throw new UserException("Error in new Registration ...");
		}

		return AuthenticationResponse.builder().accessToken("ACCESS_TOKEN").build();
	}

	// login
	public AuthenticationResponse authenticate(AuthenticationRequest authRequest, HttpServletResponse response) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

		var user = userRepository.findByUsername(authRequest.getUsername()).orElseThrow();
		var jwtToken = jwtService.generateToken(new UsersDetails(user));

		return AuthenticationResponse.builder().id(user.getId()).accessToken(jwtToken).username(user.getUsername())
				.email(user.getEmail()).fullname(user.getFullname()).mobile(user.getMobile()).build();
	}

	// send mail function user
	public String sendMailForVerify(User user) throws UnsupportedEncodingException, MessagingException {
		String otp = otpService.generateOTP(user);
		System.out.println("OTP : " + otp);
		return mailService.sendOTPMail(user, otp);
	}

	// send mail function email -> send mail abow abow function
	public String sendMailForVerify(String userEmail) throws UnsupportedEncodingException, MessagingException {
		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserException("Not Valid Email Id"));
		return sendMailForVerify(user);
	}
	
	// verify otp -- this at signup time
	public AuthenticationResponse verifyOtp(String email, String otp, HttpServletResponse response) {
		var user = this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException("Not Valid Username"));
		
		if(otpService.verifyOTP(user, otp)) {
			userRepository.updateEnabledById(user.getId(), true); //TRUE  : also update to enabled to true  
		}
		else {
			throw new UserException("Invalid otp : OTP is incorrect ❌");
		}
		
		System.out.println("OTP verified successfully ✅.");//"OTP verified successfully ✅. Please login to continue."
		var jwtToken = jwtService.generateToken(new UsersDetails(user));
		
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.id(user.getId())
				.username(user.getUsername())
				.fullname(user.getFullname())
				.email(user.getEmail())
				.mobile(user.getMobile())
				.build();
	}
	
	// verify forgot otp and : return token
	public String verifyForgotOtp(String email, String otp) {
		var user = this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException("Not Valid Username"));
		
		if(otpService.verifyOTP(user, otp)) {
			userRepository.updateEnabledById(user.getId(), true); //TRUE  : also update to enabled to true  
		}
		else {
			throw new UserException("Invalid otp : OTP is incorrect ❌");
		}
		
		var jwtToken = jwtService.generateToken(new UsersDetails(user));
		
		return jwtToken; //"OTP verified successfully ✅."
	}
	
	// refresh otp
	public String refreshOtp(String email) {
		User user = this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException("Not a Valid User")); 
		
		try {
			sendMailForVerify(user);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
			throw new UserException("Error in Otp Resend");
		}
		
		return "OTP Resended to ✉️ : "+user.getEmail();
	}
	
	// update forgot password
	public String updateForgotPassword(String username, String newPassword, String token) {
		User user = userRepository.findByUsername(username)
        		.orElseThrow(() -> new UserException("Not Valid Username ❌"));  
		
		if(!jwtService.isTokenValid(token, new UsersDetails(user))) {
			throw new UserException("Not Valid Token ❌");
		}
		
		userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
		return "Password updated successfully ✅"; 
	}

	public List<UserDto> findAllUsers() {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		
		User user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UserException("Not Valid User"));
		
		return userRepository.findAll().stream()
		.filter(u -> u != user)
		.map(UserUtils::userToUserDto)
		.toList();
	}

}
