package com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.Conversation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface JpaConversationRepository extends CrudRepository<Conversation, UUID> {
}
