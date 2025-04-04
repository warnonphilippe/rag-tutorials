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

import static com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage.COMPLETED;

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
                state.getIssueType()
        );
        log.info("Ticket creation result: {}", ticketCreationResult);
        TicketCreationResult result = ticketCreationResult.content();

        UUID ticketId = doNewTicketService.newTicket(result, state);
        state.setCurrentStage(COMPLETED);
        state.setCompletionReason("TICKET_CREATED");
        state.setTicketId(ticketId);
        stateRepository.save(state);

        return ResponseMessagePayload.createWithTicket(result.getMessage(), message, ticketId.toString());
    }
}
