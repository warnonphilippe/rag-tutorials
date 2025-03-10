package com.github.rag.tutorials.helpdesk.infrastructure.adapter.whatsapp;

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
public class WhatsAppChannelAdapter implements ChannelAdapter<String> {
    private final ProducerTemplate producerTemplate;

    @Override
    public Mono<RequestMessagePayload> adapt(String rawMessage) {
        return null;
    }

    @Override
    public Mono<Void> sendResponse(ResponseMessagePayload responsePayload) {
        return Mono.fromRunnable(() -> producerTemplate.sendBody("direct:webChatOutput", responsePayload))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Channel getChannelName() {
        return Channel.WHATSAPP;
    }
}
