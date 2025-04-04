package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.ContractVerificationResult;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ContractVerificationAgent {

    @SystemMessage("""
            You are an assistant specialized in verifying active contracts for a customer.
            Your task is to check if the customer has active contracts and, if so, help the customer select the contract for which they are requesting assistance.

            If the customer has no active contracts, you must to respond to the customer in a friendly tone that there are no active contracts and that you have forwarded his request to the sales team.
            If the customer has only one active contract, select it automatically.
            If the customer has multiple active contracts, help them select the right one by presenting the list of available contracts.

            Recognize the customer language and respond in the same language, English if not recognized.
            Respond ONLY with the ContractVerificationResult object.
            Do not add explanations or any other text beyond the requested object.
            """)
    @UserMessage("""
            User message: {{text}}
            Customer code: {{customerCode}}
            Current state: {{currentStage}}

            Information about the customer's contracts:
            {{contracts}}
            
            The contracts are in the format: contractNumber - description;
            
            Provide a ContractVerificationResult object with:
            - contractSelected: true if a contract has been selected, false otherwise
            - noActiveContracts: true if the customer has no active contracts, false otherwise
            - selectedContractNumber: Number of the selected contract (if present)
            - message: message to show to the user
            """)
    Result<ContractVerificationResult> verifyContracts(@V("text") String message,
                                                       @V("customerCode") String customerCode,
                                                       @V("currentStage") String currentStage,
                                                       @V("contracts") String contracts);

}