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

        // Determines the type of issue and the action to take
        String issueType = result.getIssueType();
        switch (issueType) {
            case "ADMINISTRATIVE":
                // Administrative issue, forward to administrative department and conclude
                state.setCurrentStage(COMPLETED);
                state.setCompletionReason("ADMINISTRATIVE_ISSUE_FORWARDED");
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);

            case "TECHNICAL":
                // Technical issue, search in the knowledge base
                state.setCurrentStage(KNOWLEDGE_BASE_SEARCH);
                conversationStateRepository.save(state);
                return knowledgeBaseSearchService.handleKnowledgeBaseSearch(message, state);

            case "ASK_MORE_INFO":
                // Handling attempts for unclassified issues
                if (state.getRetryCount() >= 2) {
                    // Unable to classify the issue after attempts, proceed to ticket creation
                    state.setCurrentStage(TICKET_CREATION);
                    conversationStateRepository.save(state);
                    return ticketCreationService.handleTicketCreation(message, state);
                }
                state.setRetryCount(state.getRetryCount() + 1);
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);

            case "OTHER":
            default:
                // Unable to classify the issue, proceed directly to ticket creation
                state.setCurrentStage(TICKET_CREATION);
                conversationStateRepository.save(state);
                return ticketCreationService.handleTicketCreation(message, state);
        }
    }
}
