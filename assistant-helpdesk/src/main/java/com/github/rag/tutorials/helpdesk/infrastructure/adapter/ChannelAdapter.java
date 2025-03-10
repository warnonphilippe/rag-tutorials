package com.github.rag.tutorials.helpdesk.infrastructure.adapter;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.Channel;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import reactor.core.publisher.Mono;

public interface ChannelAdapter<T> {
    Mono<RequestMessagePayload> adapt(T rawMessage);

    Mono<Void> sendResponse(ResponseMessagePayload responsePayload);

    Channel getChannelName();
}
