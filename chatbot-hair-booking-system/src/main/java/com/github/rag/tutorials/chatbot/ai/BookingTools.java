package com.github.rag.tutorials.chatbot.ai;

import com.github.rag.tutorials.chatbot.model.Booking;
import com.github.rag.tutorials.chatbot.service.BookingService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingTools {
    private final BookingService bookingService;

    @Tool
    public Booking getBookingDetails(String bookingNumber, String firstName, String lastName) {
        return bookingService.getBookingDetails(bookingNumber, firstName, lastName);
    }

    @Tool
    public void cancelBooking(String bookingNumber, String firstName, String lastName) {
        bookingService.cancelBooking(bookingNumber, firstName, lastName);
    }
    
    @Tool
    public Booking createBooking(String firstName, String lastName, String phoneNumber, LocalDateTime bookingTime) {
       return bookingService.createBooking(firstName, lastName, phoneNumber, bookingTime);
    }
}