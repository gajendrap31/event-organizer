package com.eventorganizer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorganizer.entity.User;
import com.eventorganizer.exception.BadRequestException;
import com.eventorganizer.exception.ConflictException;
import com.eventorganizer.exception.UnauthorizedException;
import com.eventorganizer.payload.request.LoginRequest;
import com.eventorganizer.payload.request.SignupRequest;
import com.eventorganizer.repository.UserRepository;
import com.eventorganizer.security.jwt.JwtUtils;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}
	
	private final static Logger logger = LoggerFactory.getLogger(AuthService.class);
	private static final String AUTH_ERROR_MSG = "Invalid email or password.";

	@Transactional
	public String doCustomerSignup(SignupRequest signupRequest) {
		
		return doSignup(signupRequest, "ROLE_CUSTOMER", "Customer signed up successfully.");
	}
	
	@Transactional
	public String doOrganizerSignup(SignupRequest signupRequest) {
		
		return doSignup(signupRequest, "ROLE_ORGANIZER", "Event organizer signed up successfully.");
	}
	
	private String doSignup(SignupRequest signupRequest, String role, String successMessage) {
		
		String name = signupRequest.getName().strip();
		String email = signupRequest.getEmail().strip().toLowerCase();
		String password = signupRequest.getPassword().strip();
		
		boolean exists = userRepository.existsByEmail(email);
		if(exists) {
			throw new ConflictException("User already exists with the provided email.");
		}
		
		if(password.length() < 6) {
			throw new BadRequestException("Password too small");
		}
		
		User user = new User(name, email, passwordEncoder.encode(password), role);
		
		userRepository.save(user);
		return successMessage;
	}

	public String authenticate(LoginRequest loginRequest) {
		
		logger.debug("Starting authentication for email: {}", loginRequest.getEmail());
				
		String email = loginRequest.getEmail().strip().toLowerCase();
		String password = loginRequest.getPassword().strip();
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException(AUTH_ERROR_MSG));
		
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new UnauthorizedException(AUTH_ERROR_MSG);
		}
		
		logger.info("User authenticated successfully with email: {}", email);
		return jwtUtils.generateJwtToken(user);
	}

}
