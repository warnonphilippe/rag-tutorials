package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.ContractVerificationResult;
import com.github.rag.tutorials.helpdesk.domain.contract.repository.ContractRepository;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.ConversationStateRepository;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.ContractVerificationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.github.rag.tutorials.helpdesk.domain.conversation.model.Stage.COMPLETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractVerificationService {

    private final ContractVerificationAgent contractVerificationAgent;
    private final ConversationStateRepository stateRepository;
    private final ContractRepository contractRepository;
    private final IssueClassificationService issueClassificationService;

    public ResponseMessagePayload handleContractVerification(RequestMessagePayload message, ConversationState state) {
        log.debug("Handling contract verification");
        String contracts = contractRepository.findByCustomerIdAndActiveTrue(state.getCustomerId())
                .stream()
                .map(contract -> contract.getContractNumber() + " - "+ contract.getDescription() + ";\n")
                .collect(Collectors.joining());
        log.debug("Contracts: {}", contracts);
        Result<ContractVerificationResult> contractVerificationResultResult = contractVerificationAgent.verifyContracts(
                message.getText(),
                state.getCustomerCode(),
                state.getCurrentStage().toString(),
                contracts
        );
        log.debug("Contract verification result: {}", contractVerificationResultResult);
        ContractVerificationResult result = contractVerificationResultResult.content();
        if (result.isContractSelected()) {
            log.debug("Contract selected: {}", result.getSelectedContractNumber());
            state.setCurrentStage(Stage.ISSUE_CLASSIFICATION);
            state.setSelectedContractNumber(result.getSelectedContractNumber());
            stateRepository.save(state);
            log.debug("Moving to issue classification stage");
            return issueClassificationService.handleIssueClassification(message, state);
        } else if (result.isNoActiveContracts()) {
            state.setCurrentStage(COMPLETED);
            state.setCompletionReason("NO_ACTIVE_CONTRACTS");
            stateRepository.save(state);
            log.debug("No active contracts found");
            //TODO: send email to Sales team
            log.debug("AI Assistant message: {}", message.getText());
            return ResponseMessagePayload.createSimple(result.getMessage(), message);
        }
        stateRepository.save(state);
        log.debug("No contract selected");
        log.debug("Retrying contract verification");
        log.debug("AI Assistant message: {}", message.getText());
        return ResponseMessagePayload.createSimple(result.getMessage(), message);
    }
}
