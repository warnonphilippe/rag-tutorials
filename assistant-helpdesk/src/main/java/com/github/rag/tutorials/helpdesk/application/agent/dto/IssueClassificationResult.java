package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueClassificationResult {
    private final String issueType;
    private final boolean technicalIssue;
    private final boolean administrativeIssue;
    private final String message;

    @JsonCreator
    public IssueClassificationResult(
            @JsonProperty("issueType") String issueType,
            @JsonProperty("technicalIssue") boolean technicalIssue,
            @JsonProperty("administrativeIssue") boolean administrativeIssue,
            @JsonProperty("message") String message) {
        this.issueType = issueType;
        this.technicalIssue = technicalIssue;
        this.administrativeIssue = administrativeIssue;
        this.message = message;
    }

    // Metodi factory per scenari comuni
    public static IssueClassificationResult technical(String message) {
        return IssueClassificationResult.builder()
                .issueType("TECHNICAL")
                .technicalIssue(true)
                .administrativeIssue(false)
                .message(message)
                .build();
    }

    public static IssueClassificationResult administrative(String message) {
        return IssueClassificationResult.builder()
                .issueType("ADMINISTRATIVE")
                .technicalIssue(false)
                .administrativeIssue(true)
                .message(message)
                .build();
    }

    public static IssueClassificationResult unknown(String message) {
        return IssueClassificationResult.builder()
                .issueType("UNKNOWN")
                .technicalIssue(false)
                .administrativeIssue(false)
                .message(message)
                .build();
    }
}
