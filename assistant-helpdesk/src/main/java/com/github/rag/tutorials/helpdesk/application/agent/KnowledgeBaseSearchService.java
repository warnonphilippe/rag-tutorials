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
import static com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage.TICKET_CREATION;

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
        if (result.isSolutionFound()) {
            log.debug("Solution found in the knowledge base");
            state.setCurrentStage(COMPLETED);
            state.setCompletionReason("SOLUTION_PROVIDED");
            stateRepository.save(state);
            String msg = result.getMessage();
            msg += "\n\n\nSolution found: \n\n\n" + result.getSolution();
            return ResponseMessagePayload.createSimple(msg, message);
        }
        log.debug("No solution found in the knowledge base, proceeding to ticket creation");
        state.setCurrentStage(TICKET_CREATION);
        stateRepository.save(state);
        return ticketCreationService.handleTicketCreation(message, state);
    }
}
