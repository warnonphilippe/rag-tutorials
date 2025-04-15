package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.SupervisorResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.StatefulSupervisorAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatefulSupervisorService {
    private final StatefulSupervisorAgent statefulSupervisorAgent;

    public SupervisorResult handleStatefulSupervisor(RequestMessagePayload message, ConversationState state) {
        Result<SupervisorResult> supervisorResultResult = statefulSupervisorAgent.processChatMessage(
                message.getText(),
                state.getCurrentStage().toString());
        log.info("Stateful supervisor result: {}", supervisorResultResult);
        return supervisorResultResult.content();
    }
}
