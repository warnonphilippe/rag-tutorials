package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class ResponsePayload {

    private final String id;
    private final String responseText;
    private final MessagePayload originalMessage;
    private final String responseChannel;
    private final LocalDateTime timestamp;
    private final boolean requiresHumanReview;
    private final boolean ticketCreated;
    private final String ticketId;

    public static ResponsePayload createSimple(String responseText,
                                               MessagePayload originalMessage) {
        return ResponsePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .responseText(responseText)
                .originalMessage(originalMessage)
                .responseChannel(originalMessage.getChannel())
                .timestamp(LocalDateTime.now())
                .requiresHumanReview(false)
                .ticketCreated(false)
                .build();
    }

    public static ResponsePayload createRequiringReview(String responseText,
                                                        MessagePayload originalMessage) {
        return ResponsePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .responseText(responseText)
                .originalMessage(originalMessage)
                .responseChannel(originalMessage.getChannel())
                .timestamp(LocalDateTime.now())
                .requiresHumanReview(true)
                .ticketCreated(false)
                .build();
    }

    public static ResponsePayload createWithTicket(String responseText,
                                                   MessagePayload originalMessage,
                                                   String ticketId) {
        return ResponsePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .responseText(responseText)
                .originalMessage(originalMessage)
                .responseChannel(originalMessage.getChannel())
                .timestamp(LocalDateTime.now())
                .requiresHumanReview(false)
                .ticketCreated(true)
                .ticketId(ticketId)
                .build();
    }
}
