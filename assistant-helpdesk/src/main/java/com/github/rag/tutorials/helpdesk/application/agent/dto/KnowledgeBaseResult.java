package com.github.rag.tutorials.helpdesk.application.agent.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnowledgeBaseResult {
    private final boolean solutionFound;
    private final String solution;
    private final String message;

    @JsonCreator
    public KnowledgeBaseResult(
            @JsonProperty("solutionFound") boolean solutionFound,
            @JsonProperty("solution") String solution,
            @JsonProperty("message") String message) {
        this.solutionFound = solutionFound;
        this.solution = solution;
        this.message = message;
    }

    public static KnowledgeBaseResult solutionFound(String solution, String message) {
        return KnowledgeBaseResult.builder()
                .solutionFound(true)
                .solution(solution)
                .message(message)
                .build();
    }

    public static KnowledgeBaseResult noSolutionFound(String message) {
        return KnowledgeBaseResult.builder()
                .solutionFound(false)
                .message(message)
                .build();
    }
}