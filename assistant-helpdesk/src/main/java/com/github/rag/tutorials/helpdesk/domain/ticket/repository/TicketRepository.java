package com.github.rag.tutorials.helpdesk.domain.ticket.repository;

import com.github.rag.tutorials.helpdesk.infrastructure.repository.JpaTicketRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaTicketRepository {
}
