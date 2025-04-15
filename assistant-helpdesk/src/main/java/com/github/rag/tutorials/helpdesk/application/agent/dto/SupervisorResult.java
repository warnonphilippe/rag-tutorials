package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
public class SupervisorResult {

    private final String responseMessage;
    private final boolean conversationCompleted;
    private final Stage nextStage;
    @Builder.Default
    private final List<String> agentsCalled = new ArrayList<>();
    private final String processingDetails;
    private final String ticketId;
    private final String selectedContractNumber;
    private final String issueType;
    private final String customerId;
    private final String customerCode;
    private final String customerEmail;
    private final String completionReason;

    @JsonCreator
    public SupervisorResult(
            @JsonProperty("responseMessage") String responseMessage,
            @JsonProperty("conversationCompleted") boolean conversationCompleted,
            @JsonProperty("nextStage") Stage nextStage,
            @JsonProperty("agentsCalled") List<String> agentsCalled,
            @JsonProperty("processingDetails") String processingDetails,
            @JsonProperty("ticketId") String ticketId,
            @JsonProperty("selectedContractNumber") String selectedContractNumber,
            @JsonProperty("issueType") String issueType,
            @JsonProperty("customerId") String customerId,
            @JsonProperty("customerCode") String customerCode,
            @JsonProperty("customerEmail") String customerEmail,
            @JsonProperty("completionReason") String completionReason) {
        this.responseMessage = responseMessage;
        this.conversationCompleted = conversationCompleted;
        this.nextStage = nextStage;
        this.agentsCalled = agentsCalled != null ? agentsCalled : new ArrayList<>();
        this.processingDetails = processingDetails;
        this.ticketId = ticketId;
        this.selectedContractNumber = selectedContractNumber;
        this.issueType = issueType;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerEmail = customerEmail;
        this.completionReason = completionReason;
    }
}
