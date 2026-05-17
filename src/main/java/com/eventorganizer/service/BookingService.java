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
import com.eventorganizer.exception.NotFoundException;
import com.eventorganizer.payload.request.BookingRequest;
import com.eventorganizer.repository.BookingRepository;
import com.eventorganizer.repository.EventRepository;
import com.eventorganizer.repository.UserRepository;
import com.eventorganizer.security.UserDetailsImpl;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;
	
	public BookingService(BookingRepository bookingRepository, EventRepository eventRepository,
			UserRepository userRepository, NotificationService notificationService) {
		this.bookingRepository = bookingRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}
	
	@Transactional
	public Booking bookEvent(Long eventId, BookingRequest bookingRequest) {
		
		User customer = getLoggedInUser();
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event not found."));
		
		if(event.getAvailableTickets() < bookingRequest.getTicketCount()) {
			throw new BadRequestException("Not enough tickets available.");
		}
		
		event.setAvailableTickets(event.getAvailableTickets() - bookingRequest.getTicketCount());
		event.setUpdatedAt(LocalDateTime.now());
		
		Booking booking = new Booking(customer, event, bookingRequest.getTicketCount(), LocalDateTime.now());
		Booking savedBooking = bookingRepository.save(booking);
		notificationService.sendBookingConfirmation(savedBooking);
		
		return savedBooking;
	}
	
	public List<Booking> getMyBookings() {
		
		User customer = getLoggedInUser();
		return bookingRepository.findByUserId(customer.getId());
	}
	
	private User getLoggedInUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		return userRepository.findByEmail(userDetails.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found."));
	}
}
