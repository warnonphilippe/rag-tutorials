package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueClassificationResult;
import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueType;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.IssueClassificationAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueClassificationService {

    private final IssueClassificationAgent issueClassificationAgent;
    private final ConversationStateRepository conversationStateRepository;

    public ResponseMessagePayload handleIssueClassification(RequestMessagePayload message, ConversationState state) {
        IssueClassificationResult result = issueClassificationAgent.classifyIssue(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber()
        ).content();

        state.setIssueType(IssueType.valueOf(result.getIssueType()));
        log.info("Issue classification result: {}", result);

        conversationStateRepository.save(state);
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }
}