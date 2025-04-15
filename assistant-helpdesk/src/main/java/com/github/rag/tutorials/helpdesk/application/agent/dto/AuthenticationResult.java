package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AuthenticationResult {
    private final boolean authenticated;
    private final String customerId;
    private final String customerCode;
    private final String customerEmail;
    private final String message;
    private final boolean requiresOtp;

    @JsonCreator
    public AuthenticationResult(@JsonProperty("authenticated") boolean authenticated,
                                @JsonProperty("customerId") String customerId,
                                @JsonProperty("customerCode") String customerCode,
                                @JsonProperty("customerEmail") String customerEmail,
                                @JsonProperty("message") String message,
                                @JsonProperty("requiresOtp") boolean requiresOtp) {
        this.authenticated = authenticated;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerEmail = customerEmail;
        this.message = message;
        this.requiresOtp = requiresOtp;
    }
}
