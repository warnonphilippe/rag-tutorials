package com.github.rag.tutorials.helpdesk.application.customer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerIdentificationResult {
    private boolean customerFound;
    private UUID customerId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String email;
}
