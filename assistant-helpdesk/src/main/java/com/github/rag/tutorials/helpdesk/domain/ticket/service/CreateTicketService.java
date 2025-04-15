package com.github.rag.tutorials.helpdesk.domain.ticket.service;

import com.github.rag.tutorials.helpdesk.domain.ticket.model.Ticket;
import com.github.rag.tutorials.helpdesk.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTicketService {
    private final TicketRepository ticketRepository;

    public UUID createTicket(
            String customerId,
            String issuesType,
            String description,
            String priority,
            String contractNumber) {
        log.info("Creating ticket for customerId: {}, issuesType: {}, description: {}, priority: {}, contractNumber: {}",
                customerId, issuesType, description, priority, contractNumber);
        Ticket ticket = Ticket.builder()
                .customerId(customerId)
                .issueType(issuesType)
                .contractNumber(contractNumber)
                .priority(priority)
                .description(description)
                .build();
        Ticket save = ticketRepository.save(ticket);
        return save.getId();
    }
}
