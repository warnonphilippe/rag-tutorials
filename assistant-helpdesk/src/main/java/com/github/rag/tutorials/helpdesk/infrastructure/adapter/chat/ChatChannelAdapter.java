package com.github.rag.tutorials.helpdesk.infrastructure.adapter.chat;

import com.github.rag.tutorials.helpdesk.application.chat.dto.ChatMessageRequest;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.Channel;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.ChannelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatChannelAdapter implements ChannelAdapter<ChatMessageRequest> {

    private final ProducerTemplate producerTemplate;

    @Override
    public Mono<RequestMessagePayload> adapt(ChatMessageRequest rawMessage) {
        return Mono.fromCallable(() -> {
            try {
                //TODO Metadata retrieve from ChatSessionManager
                return RequestMessagePayload.createWithChat(
                        rawMessage.getMessage(),
                        rawMessage.getSessionId(),
                        null);
            } catch (Exception e) {
                log.error("Failed to handler chat message", e);
                throw new RuntimeException("Failed to handler chat message", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> sendResponse(ResponseMessagePayload responsePayload) {
        return Mono.fromRunnable(() -> producerTemplate.sendBody("direct:webChatOutput", responsePayload))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Channel getChannelName() {
        return Channel.CHAT;
    }
}