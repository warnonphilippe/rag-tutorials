package com.github.rag.tutorials.helpdesk.domain.conversation.repository;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.Conversation;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation.JpaConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaConversationRepository {
    Optional<Conversation> findByCustomerId(String customerId);
    void deleteByCustomerId(String customerId);
}