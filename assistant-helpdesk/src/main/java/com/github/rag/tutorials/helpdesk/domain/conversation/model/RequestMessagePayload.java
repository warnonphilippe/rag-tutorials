package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class RequestMessagePayload {

    private final String id;
    private final String text;

    private final String senderEmail;
    private final String senderPhoneNumber;

    private final String senderId;
    private final Channel channel;

    private final String subject;
    private final String recipientId;

    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    private final Map<String, byte[]> attachments;

    public static RequestMessagePayload createMinimal(String text,
                                                      String senderId,
                                                      Channel channel,
                                                      Map<String, Object> metadata) {
        return RequestMessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .text(text)
                .senderId(senderId)
                .channel(channel)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .attachments(Collections.emptyMap())
                .build();
    }

    public static RequestMessagePayload createWithEmail(String text,
                                                        String senderEmail,
                                                        String senderId,
                                                        String subject,
                                                        String recipientId,
                                                        Map<String, Object> metadata,
                                                        Map<String, byte[]> attachments) {
        return RequestMessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .text(text)
                .senderEmail(senderEmail)
                .senderId(senderId)
                .channel(Channel.EMAIL)
                .subject(subject)
                .recipientId(recipientId)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .attachments(attachments)
                .build();
    }

    public static RequestMessagePayload createWithChat(String text,
                                                       String senderId,
                                                       Map<String, Object> metadata) {
        return RequestMessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .text(text)
                .senderId(senderId)
                .channel(Channel.EMAIL)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .build();
    }

    public static RequestMessagePayload createWithWhatsApp(String messageBody,
                                                           String fromNumber,
                                                           String toNumber,
                                                           Map<String, Object> metadata) {
        return RequestMessagePayload.builder()
                .id(UUID.randomUUID().toString())
                .text(messageBody)
                .senderId(fromNumber)
                .recipientId(toNumber)
                .senderPhoneNumber(fromNumber)
                .channel(Channel.WHATSAPP)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .build();
    }

    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata != null ? metadata : Collections.emptyMap());
    }

    public Map<String, byte[]> getAttachments() {
        return Collections.unmodifiableMap(attachments != null ? attachments : Collections.emptyMap());
    }
}
