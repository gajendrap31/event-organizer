package com.eventorganizer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eventorganizer.payload.request.LoginRequest;
import com.eventorganizer.payload.request.SignupRequest;
import com.eventorganizer.payload.response.SuccessResponse;
import com.eventorganizer.service.AuthService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup/customer")
	public ResponseEntity<SuccessResponse> doCustomerSignup(@RequestBody @Valid SignupRequest signupRequest) {
		
		String response = authService.doCustomerSignup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse(response));
	}
	
	@PostMapping("/signup/organizer")
	public ResponseEntity<SuccessResponse> doOrganizerSignup(@RequestBody @Valid SignupRequest signupRequest) {
		
		String response = authService.doOrganizerSignup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse(response));
	}
	
	@PostMapping("/login")
	public ResponseEntity<SuccessResponse> doLogin(@RequestBody @Valid LoginRequest loginRequest) {
		
		String response = authService.authenticate(loginRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse(response));
	}
}
