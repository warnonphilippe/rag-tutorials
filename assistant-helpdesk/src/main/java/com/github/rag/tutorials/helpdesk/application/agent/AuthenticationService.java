package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.AuthenticationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.AuthenticationAgent;
import dev.langchain4j.service.Result;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationAgent authenticationAgent;
    private final ConversationStateRepository conversationStateRepository;

    public ResponseMessagePayload handleAuthentication(RequestMessagePayload message,
                                                       ConversationState state) {
        Result<AuthenticationResult> authenticationResult = authenticationAgent.authenticate(
                message.getText(),
                message.getChannel().toString(),
                message.getSenderEmail(),
                message.getSenderPhoneNumber(),
                state.getId(),
                state.getCurrentStage().toString(),
                state.getCustomerCode());
        log.debug("Authentication result: {}", authenticationResult);
        AuthenticationResult result = authenticationResult.content();
        log.info("Authentication result: {}", result);
        if (result.getCustomerCode() != null) {
            state.setCustomerCode(result.getCustomerCode());
        }
        if (result.getCustomerEmail() != null) {
            state.setCustomerEmail(result.getCustomerEmail());
        }
        if (StringUtils.isNotBlank(result.getCustomerId())) {
            state.setCustomerId(UUID.fromString(result.getCustomerId()));
        }
        conversationStateRepository.save(state);
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }

}
