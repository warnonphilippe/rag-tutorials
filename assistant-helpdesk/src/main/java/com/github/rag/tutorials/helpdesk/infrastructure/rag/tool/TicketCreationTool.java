package com.github.rag.tutorials.helpdesk.infrastructure.rag.tool;

import com.github.rag.tutorials.helpdesk.domain.ticket.service.CreateTicketService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketCreationTool {
    private final CreateTicketService createTicketService;

    @Tool("Create a new ticket for a customer")
    public String createNewTicket(
            @P("Customer Id") String customerId,
            @P("Type of issue") String issuesType,
            @P("Description of the issue") String description,
            @P("Priority of the issue") String priority,
            @P("Contract number") String contractNumber) {
        UUID ticketId = createTicketService.createTicket(
                customerId,
                issuesType,
                description,
                priority,
                contractNumber);
        return ticketId.toString();
    }
}
