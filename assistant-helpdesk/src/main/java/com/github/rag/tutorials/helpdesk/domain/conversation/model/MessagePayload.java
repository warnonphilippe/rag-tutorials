package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class MessagePayload {

    private final String id;
    private final String text;

    private final String senderEmail;
    private final String senderPhoneNumber;

    private final String senderId;
    private final String channel;

    private final String subject;
    private final String recipientId;

    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    private final Map<String, byte[]> attachments;

    public static MessagePayload createMinimal(String text,
                                               String senderId,
                                               String channel,
                                               Map<String, Object> metadata) {
        return MessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .text(text)
                .senderId(senderId)
                .channel(channel)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .attachments(Collections.emptyMap())
                .build();
    }

    public static MessagePayload createWithEmail(String text,
                                                 String senderEmail,
                                                 String senderId,
                                                 String channel,
                                                 String subject,
                                                 String recipientId,
                                                 Map<String, Object> metadata,
                                                 Map<String, byte[]> attachments) {
        return MessagePayload.builder()
                .id(java.util.UUID.randomUUID().toString())
                .text(text)
                .senderEmail(senderEmail)
                .senderId(senderId)
                .channel(channel)
                .subject(subject)
                .recipientId(recipientId)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .attachments(attachments)
                .build();
    }

    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata != null ? metadata : Collections.emptyMap());
    }

    public Map<String, byte[]> getAttachments() {
        return Collections.unmodifiableMap(attachments != null ? attachments : Collections.emptyMap());
    }
}
