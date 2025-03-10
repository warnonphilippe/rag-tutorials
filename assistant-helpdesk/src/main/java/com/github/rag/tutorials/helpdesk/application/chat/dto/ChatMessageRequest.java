package com.github.rag.tutorials.helpdesk.application.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String sessionId;
    private String message;
}
