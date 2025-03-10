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
public class ResponseMessagePayload {

    private final String id;
    private final String responseText;
    private final RequestMessagePayload originalMessage;
    private final Channel responseChannel;
    private final LocalDateTime timestamp;
    private final boolean requiresHumanReview;
    private final boolean ticketCreated;
    private final String ticketId;

    public static ResponseMessagePayload createSimple(String responseText,
                                                      RequestMessagePayload originalMessage) {
        return ResponseMessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .responseText(responseText)
                .originalMessage(originalMessage)
                .responseChannel(originalMessage.getChannel())
                .timestamp(LocalDateTime.now())
                .requiresHumanReview(false)
                .ticketCreated(false)
                .build();
    }

    public static ResponseMessagePayload createRequiringReview(String responseText,
                                                               RequestMessagePayload originalMessage) {
        return ResponseMessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .responseText(responseText)
                .originalMessage(originalMessage)
                .responseChannel(originalMessage.getChannel())
                .timestamp(LocalDateTime.now())
                .requiresHumanReview(true)
                .ticketCreated(false)
                .build();
    }

    public static ResponseMessagePayload createWithTicket(String responseText,
                                                          RequestMessagePayload originalMessage,
                                                          String ticketId) {
        return ResponseMessagePayload.builder()
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
