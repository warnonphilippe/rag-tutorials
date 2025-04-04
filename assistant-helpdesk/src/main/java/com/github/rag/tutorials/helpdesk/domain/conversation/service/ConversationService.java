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

    @Transactional
    public Mono<ResponseMessagePayload> processMessage(RequestMessagePayload message) {
        log.debug("Processing message: {}", message);
        return Mono.just(processMessageInternal(message))
                .doOnError(e -> log.error("Error processing message.", e));
    }

    private ResponseMessagePayload processMessageInternal(RequestMessagePayload message) {
        ConversationState state = getOrCreateConversationState(message);
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
                log.debug("Conversation already completed, no further processing needed.");
                return ResponseMessagePayload.createSimple("Conversation already completed.", message);
            default:
                state.setCurrentStage(Stage.AUTHENTICATION);
                state.clearData();
                conversationStateRepository.save(state);
                return authenticationService.handleAuthentication(message, state);
        }
    }

    private ConversationState getOrCreateConversationState(RequestMessagePayload message) {
        String sessionId = getSessionId(message);
        Optional<ConversationState> stateOpt = conversationStateRepository.findById(sessionId);
        return stateOpt.orElseGet(() -> createNewConversationState(message));
    }

    private ConversationState createNewConversationState(RequestMessagePayload message) {
        ConversationState state = new ConversationState();
        state.setId(getSessionId(message));
        state.setChannel(message.getChannel());
        state.setCurrentStage(Stage.AUTHENTICATION);
        return conversationStateRepository.save(state);
    }

    private String getSessionId(RequestMessagePayload message) {
        return message.getChannel().name() + ":" + message.getSenderId();
    }
}