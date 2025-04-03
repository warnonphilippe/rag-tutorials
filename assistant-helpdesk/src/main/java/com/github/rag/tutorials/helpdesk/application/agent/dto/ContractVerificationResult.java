package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContractVerificationResult {
    private final boolean contractSelected;
    private final boolean noActiveContracts;
    private final String selectedContractNumber;
    private final String message;

    @JsonCreator
    public ContractVerificationResult(
            @JsonProperty("contractSelected") boolean contractSelected,
            @JsonProperty("noActiveContracts") boolean noActiveContracts,
            @JsonProperty("selectedContractNumber") String selectedContractNumber,
            @JsonProperty("message") String message) {
        this.contractSelected = contractSelected;
        this.noActiveContracts = noActiveContracts;
        this.selectedContractNumber = selectedContractNumber;
        this.message = message;
    }

    public static ContractVerificationResult noActiveContracts(String message) {
        return ContractVerificationResult.builder()
                .contractSelected(false)
                .noActiveContracts(true)
                .message(message)
                .build();
    }

    public static ContractVerificationResult contractSelected(String contractNumber, String message) {
        return ContractVerificationResult.builder()
                .contractSelected(true)
                .noActiveContracts(false)
                .selectedContractNumber(contractNumber)
                .message(message)
                .build();
    }

    public static ContractVerificationResult selectionNeeded(String message) {
        return ContractVerificationResult.builder()
                .contractSelected(false)
                .noActiveContracts(false)
                .message(message)
                .build();
    }
}
