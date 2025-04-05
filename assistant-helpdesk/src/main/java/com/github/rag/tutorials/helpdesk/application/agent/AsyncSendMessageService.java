package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.chat.ChatChannelAdapter;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.whatsapp.WhatsAppChannelAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncSendMessageService {
    private final WhatsAppChannelAdapter whatsAppChannelAdapter;
    private final ChatChannelAdapter chatChannelAdapter;

    public void sendMessage(ResponseMessagePayload message) {
        if (message.getResponseChannel() == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        switch (message.getResponseChannel()) {
            case WHATSAPP -> whatsAppChannelAdapter.sendResponse(message);
            case CHAT -> chatChannelAdapter.sendResponse(message);
            default -> throw new IllegalArgumentException("Unsupported channel: " + message.getResponseChannel());
        }
    }
}
