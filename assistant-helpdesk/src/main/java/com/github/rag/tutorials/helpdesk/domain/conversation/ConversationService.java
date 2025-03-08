package com.github.rag.tutorials.helpdesk.domain.conversation;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.MessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponsePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationService {
    public Mono<ResponsePayload> processMessage(MessagePayload message) {
        log.debug("Processing message: {}", message);
        return Mono.just(ResponsePayload.createSimple("Thank you for your message!", message));
    }
}