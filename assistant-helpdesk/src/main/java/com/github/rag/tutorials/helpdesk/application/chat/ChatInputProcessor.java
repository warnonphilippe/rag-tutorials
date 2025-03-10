package com.github.rag.tutorials.helpdesk.application.chat;

import com.github.rag.tutorials.helpdesk.application.chat.dto.ChatMessageRequest;
import com.github.rag.tutorials.helpdesk.domain.conversation.ConversationService;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.chat.ChatChannelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInputProcessor implements Processor {

    private final ChatChannelAdapter chatChannelAdapter;
    private final ConversationService conversationService;

    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("Processing chat message: {}", exchange);
        ChatMessageRequest message = exchange.getMessage().getBody(ChatMessageRequest.class);
        chatChannelAdapter.adapt(message)
                .flatMap(conversationService::processMessage)
                .flatMap(chatChannelAdapter::sendResponse)
                .doOnError(e -> log.error("Error processing chat message.", e))
                .subscribe();
    }
}
