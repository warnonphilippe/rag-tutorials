package com.github.rag.tutorials.helpdesk.infrastructure.adapter.email;


import com.github.rag.tutorials.helpdesk.domain.conversation.model.MessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponsePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.ChannelAdapter;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannelAdapter implements ChannelAdapter<MimeMessage> {

    private static final String CHANNEL_NAME = "email";
    private final ProducerTemplate producerTemplate;

    @Override
    public Mono<MessagePayload> adapt(MimeMessage rawMessage) {
        return Mono.fromCallable(() -> {
            try {
                MimeMessageParser parser = new MimeMessageParser(rawMessage);
                parser.parse();

                String content = parser.hasHtmlContent() ?
                        extractTextFromHtml(parser.getHtmlContent()) :
                        parser.getPlainContent();

                String senderId = parser.getFrom();

                String recipientId = parser.getTo().stream()
                        .map(Address::toString)
                        .collect(Collectors.joining(","));

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("subject", parser.getSubject());
                metadata.put("hasAttachments", !parser.getAttachmentList().isEmpty());
                metadata.put("messageId", rawMessage.getMessageID());

                if (!parser.getAttachmentList().isEmpty()) {
                    metadata.put("attachmentCount", parser.getAttachmentList().size());
                }

                return MessagePayload.createWithEmail(
                        content,
                        senderId,
                        senderId,
                        CHANNEL_NAME,
                        parser.getSubject(),
                        recipientId,
                        metadata,
                        getAttachments(parser.getAttachmentList())
                );

            } catch (Exception e) {
                log.error("Error parsing email message", e);
                throw new RuntimeException("Failed to parse email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Map<String, byte[]> getAttachments(List<DataSource> attachmentList) {
        if (attachmentList.isEmpty()) {
            return Collections.emptyMap();
        }
        return attachmentList.stream()
                .collect(Collectors.toMap(
                        DataSource::getName,
                        a -> {
                            try {
                                return IOUtils.toByteArray(a.getInputStream());
                            } catch (IOException e) {
                                log.error("Error reading attachment", e);
                                return new byte[0];
                            }
                        }
                ));

    }

    @Override
    public Mono<Void> sendResponse(ResponsePayload responsePayload) {
        return Mono.fromRunnable(() -> {
                    producerTemplate.sendBody(
                            "direct:sendEmail",
                            responsePayload);
                }).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    private String extractTextFromHtml(String html) {
        // Una implementazione semplificata - in produzione usare JSoup o simili
        return html.replaceAll("<[^>]*>", "").trim();
    }
}