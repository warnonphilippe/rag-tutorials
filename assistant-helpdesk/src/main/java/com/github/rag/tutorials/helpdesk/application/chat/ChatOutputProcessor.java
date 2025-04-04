package com.github.rag.tutorials.helpdesk.application.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatOutputProcessor implements Processor {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("Processing chat response: {}", exchange);
        ResponseMessagePayload response = exchange.getMessage().getBody(ResponseMessagePayload.class);
        messagingTemplate.convertAndSend(
                "/topic/chat." + response.getOriginalMessage().getSenderId(),
                response
        );
    }
}
