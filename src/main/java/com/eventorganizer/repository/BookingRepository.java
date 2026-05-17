package com.eventorganizer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventorganizer.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{

	List<Booking> findByUserId(Long userId);
	
	List<Booking> findByEventId(Long eventId);
	
	boolean existsByEventId(Long eventId);

}
