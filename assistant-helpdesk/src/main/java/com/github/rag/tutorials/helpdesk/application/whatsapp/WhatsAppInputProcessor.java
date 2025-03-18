package com.github.rag.tutorials.helpdesk.application.whatsapp;

import com.github.rag.tutorials.helpdesk.application.whatsapp.dto.WhatsAppMessageRequest;
import com.github.rag.tutorials.helpdesk.domain.conversation.ConversationService;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.whatsapp.WhatsAppChannelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppInputProcessor implements Processor {
    private final WhatsAppChannelAdapter whatsAppChannelAdapter;
    private final ConversationService conversationService;
    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("Processing whatsapp message: {}", exchange);
        WhatsAppMessageRequest message = exchange.getMessage().getBody(WhatsAppMessageRequest.class);
        whatsAppChannelAdapter.adapt(message)
                .flatMap(conversationService::processMessage)
                .flatMap(whatsAppChannelAdapter::sendResponse)
                .doOnError(e -> log.error("Error processing whatsapp message.", e))
                .subscribe();
    }
}
