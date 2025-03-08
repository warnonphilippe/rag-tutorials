package com.github.rag.tutorials.helpdesk.infrastructure.adapter;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.MessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponsePayload;
import reactor.core.publisher.Mono;

public interface ChannelAdapter<T> {
    Mono<MessagePayload> adapt(T rawMessage);

    Mono<Void> sendResponse(ResponsePayload responsePayload);

    String getChannelName();
}
