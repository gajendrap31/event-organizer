package com.eventorganizer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eventorganizer.entity.Booking;
import com.eventorganizer.payload.request.BookingRequest;
import com.eventorganizer.service.BookingService;

import jakarta.validation.Valid;

@RestController
public class BookingController {

	private final BookingService bookingService;
	
	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}
	
	@PostMapping("/events/{eventId}/bookings")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Booking> bookEvent(@PathVariable Long eventId, @RequestBody @Valid BookingRequest bookingRequest) {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookEvent(eventId, bookingRequest));
	}
	
	@GetMapping("/bookings/my")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<List<Booking>> getMyBookings() {
		
		return ResponseEntity.ok(bookingService.getMyBookings());
	}
}
