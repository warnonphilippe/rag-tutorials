package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.KnowledgeBaseResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.KnowledgeBaseAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeBaseSearchService {
    private final KnowledgeBaseAgent knowledgeBaseAgent;
    public ResponseMessagePayload handleKnowledgeBaseSearch(RequestMessagePayload message, ConversationState state) {
        String issueType = state.getIssueType() != null ? state.getIssueType().toString() : "";
        Result<KnowledgeBaseResult> knowledgeBaseResultResult = knowledgeBaseAgent.searchKnowledgeBase(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber(),
                issueType
        );

        KnowledgeBaseResult result = knowledgeBaseResultResult.content();
        log.info("Knowledge base search result: {}", result);
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }
}
