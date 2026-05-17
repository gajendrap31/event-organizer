package com.eventorganizer.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.eventorganizer.entity.Booking;
import com.eventorganizer.entity.Event;

@Service
public class NotificationService {

	@Async
	public void sendBookingConfirmation(Booking booking) {
		
		System.out.println("Sending booking confirmation email to " + booking.getUser().getEmail()
				+ " for event " + booking.getEvent().getTitle());
	}
	
	@Async
	public void notifyCustomersForEventUpdate(Event event, List<Booking> bookings) {
		
		for(Booking booking : bookings) {
			System.out.println("Sending event update notification to " + booking.getUser().getEmail()
					+ " for event " + event.getTitle());
		}
	}
}
