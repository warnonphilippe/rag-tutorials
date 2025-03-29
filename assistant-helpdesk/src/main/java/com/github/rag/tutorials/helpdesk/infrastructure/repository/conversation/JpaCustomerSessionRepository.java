package com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.CustomerSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;
@NoRepositoryBean
public interface JpaCustomerSessionRepository extends CrudRepository<CustomerSession, UUID> {
}
