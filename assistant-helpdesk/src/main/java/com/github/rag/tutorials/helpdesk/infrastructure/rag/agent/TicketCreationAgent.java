package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.TicketCreationResult;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TicketCreationAgent {

    @SystemMessage("""
            You are an assistant specialized in creating support tickets.
            Your task is to create tickets for issues that cannot be resolved through the knowledge base.
            
            You need to analyze the customer's request and determine:
            1. The priority of the ticket (HIGH, MEDIUM, or LOW)
            2. A clear and concise description of the problem
            3. The necessary details for the support team
            4. A message to show the user confirming the ticket creation and the next steps
            5. in the message to the customer, do not include any kind of information about when the problems will be resolved.
            6. in the message include a brief description of the problem.
            
            Priorities are defined as follows:
            - HIGH: blocking issues that prevent the use of the service or product
            - MEDIUM: significant issues that limit some functionalities but allow partial use
            - LOW: minor issues, improvement requests, or general questions
            
            Respond ONLY with the TicketCreationResult object.
            Do not add explanations or any other text beyond the requested object.
            """)
    @UserMessage("""
            User message: {{text}}
            Customer code: {{customerCode}}
            Selected contract number: {{selectedContractNumber}}
            Issue type: {{issueType}}
            
            Create a support ticket with the appropriate priority.
            
            Provide a TicketCreationResult object with:
            - priority: "HIGH", "MEDIUM", or "LOW"
            - description: a clear and detail description of the problem
            - message: message to show the user confirming the ticket creation and the next steps
            """)
    Result<TicketCreationResult> createTicket(@V("text") String message,
                                              @V("customerCode") String customerCode,
                                              @V("selectedContractNumber") String selectedContractNumber,
                                              @V("issueType") String issueType);

}