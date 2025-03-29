package com.github.rag.tutorials.helpdesk.domain.conversation.service;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.AuthenticationAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationService {
    
    public Mono<ResponseMessagePayload> processMessage(RequestMessagePayload message) {
        log.debug("Processing message: {}", message);
        return Mono.just(ResponseMessagePayload.createSimple("Thank you for your message!", message));
    }
}