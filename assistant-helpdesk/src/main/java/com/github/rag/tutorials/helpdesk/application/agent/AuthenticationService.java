package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.AuthenticationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.AuthenticationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationAgent authenticationAgent;
    private final ContractVerificationService contractVerificationService;
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
        if (result.isAuthenticated()) {
            log.debug("Authenticated customerId: {}", result.getCustomerId());
            log.debug("Authenticated customer code: {}", result.getCustomerCode());
            log.debug("Authenticated customer email: {}", result.getCustomerEmail());
            state.setCurrentStage(Stage.CONTRACT_VERIFICATION);
            state.setCustomerId(UUID.fromString(result.getCustomerId()));
            state.setCustomerCode(result.getCustomerCode());
            state.setCustomerEmail(result.getCustomerEmail());
            conversationStateRepository.save(state);
            log.debug("Customer authenticated successfully");
            log.debug("Moving to contract verification stage");
            return contractVerificationService.handleContractVerification(message, state);
        }
        log.debug("Authentication failed");
        log.debug("Retrying authentication");
        conversationStateRepository.save(state);
        log.debug("AI Assistant message: {}", message.getText());
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }

}
