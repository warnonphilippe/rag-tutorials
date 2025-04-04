package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueClassificationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.IssueClassificationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueClassificationService {

    private final IssueClassificationAgent issueClassificationAgent;
    private final ConversationStateRepository conversationStateRepository;
    private final KnowledgeBaseSearchService knowledgeBaseSearchService;
    private final TicketCreationService ticketCreationService;

    public ResponseMessagePayload handleIssueClassification(RequestMessagePayload message, ConversationState state) {
        Result<IssueClassificationResult> issueClassificationResultResult = issueClassificationAgent.classifyIssue(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber()
        );
        IssueClassificationResult result = issueClassificationResultResult.content();
        state.setIssueType(result.getIssueType());

        log.debug("Determines the type of issue and the action to take");
        log.info("Issue classification result: {}", result);
        String issueType = result.getIssueType();
        switch (issueType) {
            case "ADMINISTRATIVE":
                log.debug("Administrative issue, forwarding to the administrative team");
                state.setCurrentStage(COMPLETED);
                state.setCompletionReason("ADMINISTRATIVE_ISSUE_FORWARDED");
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);
            case "TECHNICAL":
                log.debug("Technical issue, forwarding to the technical team");
                state.setCurrentStage(KNOWLEDGE_BASE_SEARCH);
                conversationStateRepository.save(state);
                return knowledgeBaseSearchService.handleKnowledgeBaseSearch(message, state);
            case "ASK_MORE_INFO":
                log.debug("Requesting more information from the customer");
                if (state.getRetryCount() >= 2) {
                    log.debug("Customer has already been asked for more information 2 times, proceeding to ticket creation");
                    state.setCurrentStage(TICKET_CREATION);
                    conversationStateRepository.save(state);
                    return ticketCreationService.handleTicketCreation(message, state);
                }
                state.setRetryCount(state.getRetryCount() + 1);
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);
            case "OTHER":
            default:
                log.debug("Other issue type, proceeding to ticket creation");
                state.setCurrentStage(TICKET_CREATION);
                conversationStateRepository.save(state);
                return ticketCreationService.handleTicketCreation(message, state);
        }
    }
}
