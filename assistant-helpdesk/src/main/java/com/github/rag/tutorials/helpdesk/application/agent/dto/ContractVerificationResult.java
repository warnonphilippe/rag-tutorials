package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
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
}
