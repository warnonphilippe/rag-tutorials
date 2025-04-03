package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCreationResult {
    private final String priority;
    private final String message;

    @JsonCreator
    public TicketCreationResult(
            @JsonProperty("priority") String priority, 
            @JsonProperty("message") String message) {
        this.priority = priority;
        this.message = message;
    }

    public static TicketCreationResult highPriority(String message) {
        return TicketCreationResult.builder()
                .priority("high")
                .message(message)
                .build();
    }

    public static TicketCreationResult mediumPriority(String message) {
        return TicketCreationResult.builder()
                .priority("medium")
                .message(message)
                .build();
    }

    public static TicketCreationResult lowPriority(String message) {
        return TicketCreationResult.builder()
                .priority("low")
                .message(message)
                .build();
    }
}