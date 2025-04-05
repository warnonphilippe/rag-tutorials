package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.KnowledgeBaseResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.KnowledgeBaseAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage.COMPLETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeBaseSearchService {
    private final KnowledgeBaseAgent knowledgeBaseAgent;
    private final ConversationStateRepository stateRepository;
    private final TicketCreationService ticketCreationService;

    public ResponseMessagePayload handleKnowledgeBaseSearch(RequestMessagePayload message, ConversationState state) {
        Result<KnowledgeBaseResult> knowledgeBaseResultResult = knowledgeBaseAgent.searchKnowledgeBase(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber(),
                state.getIssueType()
        );

        KnowledgeBaseResult result = knowledgeBaseResultResult.content();
        log.info("Knowledge base search result: {}", result);
        if (result.isCustomerSatisfiedWithTheSolution()) {
            log.debug("Solution found in the knowledge base");
            state.setCurrentStage(COMPLETED);
            state.setCompletionReason("SOLUTION_PROVIDED");
            state.setRetryCount(0);
            stateRepository.save(state);
            return ResponseMessagePayload.createSimple(result.getMessage(), message);
        }
        log.debug("Solution not found in the knowledge base, asking for more information");
        if (state.getRetryCount() >= 3) {
            log.debug("Customer has already been asked for more information 2 times, proceeding to ticket creation");
            state.setCompletionReason("NO_SOLUTION_FOUND");
            stateRepository.save(state);
            return ticketCreationService.handleTicketCreation(message, state);
        }
        state.setRetryCount(state.getRetryCount() + 1);
        stateRepository.save(state);
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }
}
