package com.github.rag.tutorials.helpdesk.domain.ticket.service;

import com.github.rag.tutorials.helpdesk.application.agent.dto.TicketCreationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.ticket.model.Ticket;
import com.github.rag.tutorials.helpdesk.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoNewTicketService {
    private final TicketRepository ticketRepository;
    public UUID newTicket(TicketCreationResult ticketResult, ConversationState conversationState) {
        Ticket ticket = Ticket.builder()
                .customerId(conversationState.getCustomerId().toString())
                .issueType(conversationState.getIssueType())
                .contractNumber(conversationState.getSelectedContractNumber())
                .priority(ticketResult.getPriority())
                .description(ticketResult.getMessage())
                .build();
        Ticket save = ticketRepository.save(ticket);
        return save.getId();
    }
}
