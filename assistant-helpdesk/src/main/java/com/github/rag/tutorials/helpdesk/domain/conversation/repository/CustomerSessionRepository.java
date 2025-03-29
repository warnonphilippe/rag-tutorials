package com.github.rag.tutorials.helpdesk.domain.conversation.repository;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.CustomerSession;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.conversation.JpaCustomerSessionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerSessionRepository extends JpaCustomerSessionRepository {
    Optional<CustomerSession> findByCustomerCode(String customerCode);
}