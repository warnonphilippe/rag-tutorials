package com.github.rag.tutorials.helpdesk.application.whatsapp;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppOutputProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("Processing whatsapp response: {}", exchange);
        ResponseMessagePayload response = exchange.getMessage().getBody(ResponseMessagePayload.class);
        log.info("Sending response to whatsapp: {}", response);
    }
}
