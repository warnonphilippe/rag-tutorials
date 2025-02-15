package com.github.rag.tutorials.chatbot.repository;

import com.github.rag.tutorials.chatbot.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends CrudRepository<Booking, UUID> {
    Optional<Booking> findByBookingNumber(String bookingNumber);

    boolean existsByBookingDate(LocalDateTime bookingDate);
}