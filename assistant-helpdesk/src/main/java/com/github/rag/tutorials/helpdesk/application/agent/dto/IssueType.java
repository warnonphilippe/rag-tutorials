package com.github.rag.tutorials.helpdesk.application.agent.dto;

public enum IssueType {
    TECHNICAL,
    ADMINISTRATIVE,
    UNKNOWN;

    public static IssueType fromString(String issueType) {
        try {
            return IssueType.valueOf(issueType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
