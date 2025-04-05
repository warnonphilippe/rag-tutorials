package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueClassificationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.IssueClassificationAgent;
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
    private final AsyncSendMessageService asyncSendMessageService;

    public ResponseMessagePayload handleIssueClassification(RequestMessagePayload message, ConversationState state) {
        IssueClassificationResult result = issueClassificationAgent.classifyIssue(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber()
        ).content();
        
        state.setIssueType(result.getIssueType());
        log.info("Issue classification result: {}", result);

        switch (result.getIssueType()) {
            case "ADMINISTRATIVE":
                state.setCurrentStage(COMPLETED);
                state.setCompletionReason("ADMINISTRATIVE_ISSUE_FORWARDED");
                state.setRetryCount(0);
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);
                
            case "TECHNICAL":
                state.setCurrentStage(KNOWLEDGE_BASE_SEARCH);
                state.setRetryCount(0);
                conversationStateRepository.save(state);
                asyncSendMessageService.sendMessage(ResponseMessagePayload.createSimple(result.getMessage(), message));
                return knowledgeBaseSearchService.handleKnowledgeBaseSearch(message, state);
                
            case "ASK_MORE_INFO":
                if (state.getRetryCount() >= 2) {
                    state.setCurrentStage(TICKET_CREATION);
                    conversationStateRepository.save(state);
                    asyncSendMessageService.sendMessage(ResponseMessagePayload.createSimple(result.getMessage(), message));
                    return ticketCreationService.handleTicketCreation(message, state);
                }
                state.setRetryCount(state.getRetryCount() + 1);
                conversationStateRepository.save(state);
                return ResponseMessagePayload.createSimple(result.getMessage(), message);
                
            default:
                log.debug("Other issue type, proceeding to ticket creation");
                state.setCurrentStage(TICKET_CREATION);
                conversationStateRepository.save(state);
                asyncSendMessageService.sendMessage(ResponseMessagePayload.createSimple(result.getMessage(), message));
                return ticketCreationService.handleTicketCreation(message, state);
        }
    }
}