package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.TicketCreationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.domain.ticket.service.DoNewTicketService;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.TicketCreationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketCreationService {
    private final TicketCreationAgent ticketCreationAgent;
    private final DoNewTicketService doNewTicketService;
    private final ConversationStateRepository stateRepository;

    public ResponseMessagePayload handleTicketCreation(RequestMessagePayload message, ConversationState state) {
        Result<TicketCreationResult> ticketCreationResult = ticketCreationAgent.createTicket(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber(),
                state.getIssueType().toString()
        );
        TicketCreationResult result = ticketCreationResult.content();
        log.info("Ticket creation result: {}", result);

        UUID ticketId = doNewTicketService.newTicket(result, state);
        state.setTicketId(ticketId);
        stateRepository.save(state);

        String messageResponse = result.getMessage() + "\n" +
                                 "Ticket ID: " + ticketId.toString();
        return ResponseMessagePayload.createWithTicket(messageResponse, message, ticketId.toString());
    }
}
