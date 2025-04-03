package com.github.rag.tutorials.helpdesk.infrastructure.adapter.whatsapp;

import com.github.rag.tutorials.helpdesk.application.whatsapp.dto.WhatsAppMessageRequest;
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
public class WhatsAppChannelAdapter implements ChannelAdapter<WhatsAppMessageRequest> {
    private final ProducerTemplate producerTemplate;

    @Override
    public Mono<RequestMessagePayload> adapt(WhatsAppMessageRequest rawMessage) {
        return Mono.fromCallable(() -> {
            try {
                //TODO Metadata retrieve from ChatSessionManager
                return RequestMessagePayload.createWithWhatsApp(
                        rawMessage.getMessageBody(),
                        rawMessage.getFromNumber(),
                        rawMessage.getToNumber(),
                        null);
            } catch (Exception e) {
                log.error("Error parsing email message", e);
                throw new RuntimeException("Failed to parse email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> sendResponse(ResponseMessagePayload responsePayload) {
        return Mono.fromRunnable(() -> producerTemplate.sendBody("direct:whatsAppOutput", responsePayload))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Channel getChannelName() {
        return Channel.WHATSAPP;
    }
}
