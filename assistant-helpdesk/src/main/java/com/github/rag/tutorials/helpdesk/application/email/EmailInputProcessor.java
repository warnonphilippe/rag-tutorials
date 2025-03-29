package com.github.rag.tutorials.helpdesk.application.email;

import com.github.rag.tutorials.helpdesk.domain.conversation.service.ConversationService;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.email.EmailChannelAdapter;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mail.MailMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailInputProcessor implements Processor {

    private final EmailChannelAdapter emailChannelAdapter;
    private final ConversationService conversationService;

    @Override
    public void process(Exchange exchange) {
        log.debug("Processing email message: {}", exchange);
        MailMessage mailMessage = exchange.getMessage().getBody(MailMessage.class);
        MimeMessage message = (MimeMessage) mailMessage.getMessage();
        emailChannelAdapter.adapt(message)
                .flatMap(conversationService::processMessage)
                .flatMap(emailChannelAdapter::sendResponse)
                .doOnError(e -> log.error("Error processing email", e))
                .subscribe();
    }
}