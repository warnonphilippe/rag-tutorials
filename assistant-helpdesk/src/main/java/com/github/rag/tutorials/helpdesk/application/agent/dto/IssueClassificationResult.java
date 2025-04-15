package com.github.rag.tutorials.helpdesk.application.agent.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
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
}
