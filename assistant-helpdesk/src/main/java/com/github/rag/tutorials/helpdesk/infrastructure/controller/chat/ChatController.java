package com.github.rag.tutorials.helpdesk.infrastructure.controller.chat;

import com.github.rag.tutorials.helpdesk.application.chat.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ProducerTemplate producerTemplate;

    @MessageMapping("/chat.message")
    public void handleChatMessage(@Payload ChatMessageRequest message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.debug("Received message from WebSocket session {}: {}", sessionId, message.getMessage());
        producerTemplate.sendBody("direct:webChatInput", message);
    }

}