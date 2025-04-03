package com.github.rag.tutorials.helpdesk.domain.conversation.service;

import com.github.rag.tutorials.helpdesk.application.agent.*;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Service that orchestrates the conversation flow between various specialized agents.
 * Each agent handles a specific phase of the process.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationService {

    private final AuthenticationService authenticationService;
    private final ContractVerificationService contractVerificationService;
    private final IssueClassificationService issueClassificationService;
    private final KnowledgeBaseSearchService knowledgeBaseSearchService;
    private final TicketCreationService ticketCreationService;
    private final ConversationStateRepository conversationStateRepository;

    /**
     * Processes an incoming message, orchestrating the flow through various specialized agents
     * based on the current state of the conversation.
     */
    @Transactional
    public Mono<ResponseMessagePayload> processMessage(RequestMessagePayload message) {
        return Mono.just(processMessageInternal(message));
    }
    
    private ResponseMessagePayload processMessageInternal(RequestMessagePayload message) {
        log.debug("Processing message: {}", message);
        // Gets or creates a conversation state for the sender
        ConversationState state = getOrCreateConversationState(message);

        // Determines which agent should handle the message based on the current state
        switch (state.getCurrentStage()) {
            case AUTHENTICATION:
                return authenticationService.handleAuthentication(message, state);
            case CONTRACT_VERIFICATION:
                return contractVerificationService.handleContractVerification(message, state);
            case ISSUE_CLASSIFICATION:
                return issueClassificationService.handleIssueClassification(message, state);
            case KNOWLEDGE_BASE_SEARCH:
                return knowledgeBaseSearchService.handleKnowledgeBaseSearch(message, state);
            case TICKET_CREATION:
                return ticketCreationService.handleTicketCreation(message, state);
            case COMPLETED:
                return knowledgeBaseSearchService.handleKnowledgeBaseSearch(message, state);
            default:
                state.setCurrentStage(Stage.AUTHENTICATION);
                state.clearData();
                conversationStateRepository.save(state);
                return authenticationService.handleAuthentication(message, state);
        }
    }

    /**
     * Gets or creates the state of a conversation for a given message.
     */
    private ConversationState getOrCreateConversationState(RequestMessagePayload message) {
        String sessionId = getSessionId(message);
        Optional<ConversationState> stateOpt = conversationStateRepository.findById(sessionId);
        return stateOpt.orElseGet(() -> createNewConversationState(message));
    }

    /**
     * Creates a new conversation state for a message.
     */
    private ConversationState createNewConversationState(RequestMessagePayload message) {
        ConversationState state = new ConversationState();
        state.setId(getSessionId(message));
        state.setChannel(message.getChannel());
        state.setCurrentStage(Stage.AUTHENTICATION);
        state.setCustomerCode("");
        state.setCustomerEmail("");
        return conversationStateRepository.save(state);
    }


    /**
     * Generates a unique session ID for a message.
     */
    private String getSessionId(RequestMessagePayload message) {
        return message.getChannel().name() + ":" + message.getSenderId();
    }
}