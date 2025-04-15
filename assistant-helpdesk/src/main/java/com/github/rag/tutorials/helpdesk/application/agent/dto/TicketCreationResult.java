package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TicketCreationResult {
    private final String priority;
    private final String description;
    private final String message;

    @JsonCreator
    public TicketCreationResult(
            @JsonProperty("priority") String priority, 
            @JsonProperty("description") String description,
            @JsonProperty("message") String message) {
        this.priority = priority;
        this.description = description;
        this.message = message;
    }
}