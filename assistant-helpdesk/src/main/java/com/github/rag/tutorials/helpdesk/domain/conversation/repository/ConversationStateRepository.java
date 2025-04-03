package com.github.rag.tutorials.helpdesk.domain.conversation.repository;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation.JpaConversationStateRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationStateRepository extends JpaConversationStateRepository {
    ConversationState save(ConversationState conversationState);
}
