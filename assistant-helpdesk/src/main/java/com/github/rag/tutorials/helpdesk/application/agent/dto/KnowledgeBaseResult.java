package com.github.rag.tutorials.helpdesk.application.agent.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class KnowledgeBaseResult {
    private final boolean solutionFound;
    private final boolean customerSatisfiedWithTheSolution;
    private final String message;

    @JsonCreator
    public KnowledgeBaseResult(
            @JsonProperty("solutionFound") boolean solutionFound,
            @JsonProperty("customerSatisfiedWithTheSolution") boolean customerSatisfiedWithTheSolution,
            @JsonProperty("message") String message) {
        this.solutionFound = solutionFound;
        this.customerSatisfiedWithTheSolution = customerSatisfiedWithTheSolution;
        this.message = message;
    }
}