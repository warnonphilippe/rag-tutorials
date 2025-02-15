package com.github.rag.tutorials.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "booking")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    @Column(name = "booking_number", nullable = false)
    private String bookingNumber;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @PrePersist
    private void ensureBookingNumber() {
        if (bookingNumber == null) {
            bookingNumber = String.format("B%04d", UUID.randomUUID().hashCode() & Integer.MAX_VALUE);
        }
    }
}