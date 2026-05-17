package com.eventorganizer.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

	@NotNull(message = "Ticket count is required")
	@Min(value = 1, message = "Ticket count must be at least 1")
	private Integer ticketCount;

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}
}
