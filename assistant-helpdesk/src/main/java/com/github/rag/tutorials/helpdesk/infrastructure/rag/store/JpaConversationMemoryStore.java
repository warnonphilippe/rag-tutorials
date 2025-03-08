package com.github.rag.tutorials.helpdesk.infrastructure.rag.store;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.Conversation;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationRepository;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaConversationMemoryStore implements ChatMemoryStore {
    private final ConversationRepository conversationRepository;

    @Override
    public List<ChatMessage> getMessages(Object customerId) {
        return messagesFromJson(conversationRepository.findByCustomerId(customerId.toString())
                .map(Conversation::getMessages)
                .orElse("[]"));
    }

    @Override
    public void updateMessages(Object customerId, List<ChatMessage> list) {
        Optional<Conversation> message = conversationRepository.findByCustomerId(customerId.toString());
        if (message.isEmpty()) {
            conversationRepository.save(new Conversation(customerId.toString(), messagesToJson(list)));
        } else {
            message.ifPresent(m -> {
                m.setMessages(messagesToJson(list));
                conversationRepository.save(m);
            });
        }
    }

    @Override
    public void deleteMessages(Object customerId) {
        conversationRepository.deleteByCustomerId(customerId.toString());
    }
}
