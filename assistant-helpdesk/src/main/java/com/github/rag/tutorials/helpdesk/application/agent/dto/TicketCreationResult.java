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
    private final String ticketId;
    private final String customerId;
    private final String issuesType;
    private final String contractNumber;
    private final String priority;
    private final String description;
    private final String message;

    @JsonCreator
    public TicketCreationResult(
            @JsonProperty("priority") String priority,
            @JsonProperty("description") String description,
            @JsonProperty("message") String message,
            @JsonProperty("ticketId") String ticketId,
            @JsonProperty("customerId") String customerId,
            @JsonProperty("issuesType") String issuesType,
            @JsonProperty("contractNumber") String contractNumber) {
        this.ticketId = ticketId;
        this.priority = priority;
        this.description = description;
        this.message = message;
        this.customerId = customerId;
        this.issuesType = issuesType;
        this.contractNumber = contractNumber;
    }
}