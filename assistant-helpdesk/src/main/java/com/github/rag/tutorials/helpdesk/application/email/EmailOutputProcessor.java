package com.github.rag.tutorials.helpdesk.application.email;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class EmailOutputProcessor implements Processor {

    private final String footer;
    private final String defaultSubject;

    public EmailOutputProcessor(@Value("${mail.support.footer}") String footer,
                                @Value("${mail.support.defaultSubject}") String defaultSubject) {
        this.footer = footer;
        this.defaultSubject = defaultSubject;
    }

    @Override
    public void process(Exchange exchange) {
        ResponseMessagePayload responsePayload = exchange.getIn().getBody(ResponseMessagePayload.class);
        if (responsePayload == null) {
            log.error("ResponsePayload is null, cannot process email response");
            return;
        }
        RequestMessagePayload originalMessage = responsePayload.getOriginalMessage();
        if (originalMessage == null) {
            log.error("Original message is null, cannot maintain conversation thread");
            return;
        }
        prepareEmailResponse(exchange, responsePayload, originalMessage);
        log.info("Email response prepared for: {}", originalMessage.getSenderEmail());
    }

    private void prepareEmailResponse(Exchange exchange,
                                      ResponseMessagePayload responsePayload,
                                      RequestMessagePayload originalMessage) {
        Map<String, Object> originalHeaders = originalMessage.getMetadata();
        String messageId = Optional.ofNullable(originalHeaders.get("Message-ID"))
                .map(Object::toString)
                .orElse(null);

        String references = Optional.ofNullable(originalHeaders.get("References"))
                .map(Object::toString)
                .orElse("");

        if (messageId != null) {
            if (!references.contains(messageId)) {
                references = references.isEmpty() ? messageId : references + " " + messageId;
            }
        }

        exchange.getIn().setHeader("To", originalMessage.getSenderEmail());
        exchange.getIn().setHeader("Subject", "Re: " + getSubject(originalMessage));

        if (messageId != null) {
            exchange.getIn().setHeader("In-Reply-To", messageId);
        }

        if (!references.isEmpty()) {
            exchange.getIn().setHeader("References", references);
        }

        String responseText = formatResponseText(responsePayload.getResponseText());
        exchange.getIn().setBody(responseText);

        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain; charset=UTF-8");
    }

    private String getSubject(RequestMessagePayload originalMessage) {
        String subject = Optional.ofNullable(originalMessage.getSubject())
                .orElse(defaultSubject);

        while (subject.toLowerCase().startsWith("re:")) {
            subject = subject.substring(3).trim();
        }

        return subject;
    }

    private String formatResponseText(String responseText) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return now.format(formatter) + ":\n\n" +
                responseText + "\n\n" +
                footer;
    }
}