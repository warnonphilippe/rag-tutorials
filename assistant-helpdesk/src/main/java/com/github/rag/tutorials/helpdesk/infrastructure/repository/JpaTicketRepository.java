package com.github.rag.tutorials.helpdesk.infrastructure.repository;

import com.github.rag.tutorials.helpdesk.domain.ticket.model.Ticket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@NoRepositoryBean
public interface JpaTicketRepository extends CrudRepository<Ticket, UUID> {
}
