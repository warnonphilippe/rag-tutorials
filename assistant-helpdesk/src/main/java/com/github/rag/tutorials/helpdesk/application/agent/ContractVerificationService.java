package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.ContractVerificationResult;
import com.github.rag.tutorials.helpdesk.domain.contract.repository.ContractRepository;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.ContractVerificationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractVerificationService {

    private final ContractVerificationAgent contractVerificationAgent;
    private final ConversationStateRepository stateRepository;
    private final ContractRepository contractRepository;

    public ResponseMessagePayload handleContractVerification(RequestMessagePayload message, ConversationState state) {
        log.debug("Handling contract verification");
        log.debug("CustomerId: {}", state.getCustomerId());
        log.debug("CustomerCode: {}", state.getCustomerCode());
        log.debug("CustomerEmail: {}", state.getCustomerEmail());
        
        String contracts = contractRepository.findByCustomerIdAndActiveTrue(state.getCustomerId())
                .stream()
                .map(contract -> contract.getContractNumber() + " - " + contract.getDescription() + ";\n")
                .collect(Collectors.joining());
        log.debug("Contracts: {}", contracts);
        Result<ContractVerificationResult> contractVerificationResultResult = contractVerificationAgent.verifyContracts(
                message.getText(),
                state.getCustomerCode(),
                state.getCurrentStage().toString(),
                contracts
        );
        ContractVerificationResult result = contractVerificationResultResult.content();
        log.info("Contract verification result: {}", result);
        state.setSelectedContractNumber(result.getSelectedContractNumber());
        stateRepository.save(state);
        log.debug("AI Assistant message: {}", message.getText());
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }
}
