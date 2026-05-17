package com.eventorganizer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorganizer.entity.Booking;
import com.eventorganizer.entity.Event;
import com.eventorganizer.entity.User;
import com.eventorganizer.exception.BadRequestException;
import com.eventorganizer.exception.ForbiddenException;
import com.eventorganizer.exception.NotFoundException;
import com.eventorganizer.payload.request.EventRequest;
import com.eventorganizer.repository.BookingRepository;
import com.eventorganizer.repository.EventRepository;
import com.eventorganizer.repository.UserRepository;
import com.eventorganizer.security.UserDetailsImpl;

@Service
public class EventService {

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final NotificationService notificationService;
	
	public EventService(EventRepository eventRepository, UserRepository userRepository,
			BookingRepository bookingRepository, NotificationService notificationService) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.bookingRepository = bookingRepository;
		this.notificationService = notificationService;
	}
	
	public List<Event> getAllEvents() {
		
		return eventRepository.findAll();
	}
	
	public Event getEvent(Long eventId) {
		
		return eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event not found."));
	}
	
	public List<Event> getMyEvents() {
		
		User organizer = getLoggedInUser();
		return eventRepository.findByOrganizerId(organizer.getId());
	}
	
	@Transactional
	public Event createEvent(EventRequest eventRequest) {
		
		User organizer = getLoggedInUser();
		LocalDateTime now = LocalDateTime.now();
		
		Event event = new Event(
				eventRequest.getTitle().strip(),
				eventRequest.getDescription().strip(),
				eventRequest.getLocation().strip(),
				eventRequest.getEventDate(),
				eventRequest.getTotalTickets(),
				eventRequest.getTotalTickets(),
				organizer,
				now,
				now);
		
		return eventRepository.save(event);
	}
	
	@Transactional
	public Event updateEvent(Long eventId, EventRequest eventRequest) {
		
		User organizer = getLoggedInUser();
		Event event = getEvent(eventId);
		validateOrganizer(event, organizer);
		
		int bookedTickets = event.getTotalTickets() - event.getAvailableTickets();
		if(eventRequest.getTotalTickets() < bookedTickets) {
			throw new BadRequestException("Total tickets cannot be less than already booked tickets.");
		}
		
		event.setTitle(eventRequest.getTitle().strip());
		event.setDescription(eventRequest.getDescription().strip());
		event.setLocation(eventRequest.getLocation().strip());
		event.setEventDate(eventRequest.getEventDate());
		event.setTotalTickets(eventRequest.getTotalTickets());
		event.setAvailableTickets(eventRequest.getTotalTickets() - bookedTickets);
		event.setUpdatedAt(LocalDateTime.now());
		
		Event updatedEvent = eventRepository.save(event);
		List<Booking> bookings = bookingRepository.findByEventId(eventId);
		notificationService.notifyCustomersForEventUpdate(updatedEvent, bookings);
		
		return updatedEvent;
	}
	
	@Transactional
	public String deleteEvent(Long eventId) {
		
		User organizer = getLoggedInUser();
		Event event = getEvent(eventId);
		validateOrganizer(event, organizer);
		
		if(bookingRepository.existsByEventId(eventId)) {
			throw new BadRequestException("Event with bookings cannot be deleted.");
		}
		
		eventRepository.delete(event);
		return "Event deleted successfully.";
	}
	
	private User getLoggedInUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		return userRepository.findByEmail(userDetails.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found."));
	}
	
	private void validateOrganizer(Event event, User organizer) {
		
		if(!event.getOrganizer().getId().equals(organizer.getId())) {
			throw new ForbiddenException("You are not allowed to manage this event.");
		}
	}
}
