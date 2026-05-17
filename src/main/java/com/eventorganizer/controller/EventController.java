package com.eventorganizer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorganizer.entity.Event;
import com.eventorganizer.payload.request.EventRequest;
import com.eventorganizer.payload.response.SuccessResponse;
import com.eventorganizer.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {

	private final EventService eventService;
	
	public EventController(EventService eventService) {
		this.eventService = eventService;
	}
	
	@GetMapping
	public ResponseEntity<List<Event>> getAllEvents() {
		
		return ResponseEntity.ok(eventService.getAllEvents());
	}
	
	@GetMapping("/{eventId}")
	public ResponseEntity<Event> getEvent(@PathVariable Long eventId) {
		
		return ResponseEntity.ok(eventService.getEvent(eventId));
	}
	
	@GetMapping("/my")
	@PreAuthorize("hasRole('ORGANIZER')")
	public ResponseEntity<List<Event>> getMyEvents() {
		
		return ResponseEntity.ok(eventService.getMyEvents());
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ORGANIZER')")
	public ResponseEntity<Event> createEvent(@RequestBody @Valid EventRequest eventRequest) {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequest));
	}
	
	@PutMapping("/{eventId}")
	@PreAuthorize("hasRole('ORGANIZER')")
	public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody @Valid EventRequest eventRequest) {
		
		return ResponseEntity.ok(eventService.updateEvent(eventId, eventRequest));
	}
	
	@DeleteMapping("/{eventId}")
	@PreAuthorize("hasRole('ORGANIZER')")
	public ResponseEntity<SuccessResponse> deleteEvent(@PathVariable Long eventId) {
		
		String response = eventService.deleteEvent(eventId);
		return ResponseEntity.ok(new SuccessResponse(response));
	}
}
