package com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaConversationStateRepository extends CrudRepository<ConversationState, String> {
}
