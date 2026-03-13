package com.taskmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanagement.auth.dto.AuthResponse;
import com.taskmanagement.auth.dto.LoginRequest;
import com.taskmanagement.auth.dto.RegisterRequest;
import com.taskmanagement.auth.dto.UserResponse;
import com.taskmanagement.auth.entity.User;
import com.taskmanagement.auth.repository.UserRepository;
import com.taskmanagement.auth.security.JwtUtil;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	public AuthResponse register(RegisterRequest request) {

		User user = User.builder().name(request.getName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role("USER").build();

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getEmail());

		UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail());

		return new AuthResponse("User registered successfully", token, userResponse);
	}

	public AuthResponse login(LoginRequest request) {

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		String token = jwtUtil.generateToken(user.getEmail());

		UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail());

		return new AuthResponse("Login successful", token, userResponse);
	}
}