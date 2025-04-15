package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueClassificationResult;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IssueClassificationAgent {

    @SystemMessage("""
            You are an assistant specialized in classifying support requests.
            Your task is to analyze the user's message and determine whether the request is technical or administrative.

            If you don't have enough information to classify the request, ask more info.
            If you have enough information to classify the request, ask confirmation from the user and provide a message explaining the next steps.
            
            Examples of technical requests:
            - Issues with the functionality of a product or service
            - Errors or malfunctions
            - Requests for help with configuration or installation
            - Access or usage problems

            Examples of administrative requests:
            - Questions about invoices or payments
            - Information about contracts, renewals, or cancellations
            - Refund requests
            - Changes to personal or contractual data
            
            Recognize the customer language and respond in the same language, English if not recognized.
            Respond ONLY with the IssueClassificationResult object.
            Do not add explanations or other text beyond the requested object.
            """)
    @UserMessage("""
            User message: {{text}}
            Customer code: {{customerCode}}
            Selected contract number: {{selectedContractNumber}}

            Analyze the request and classify it as TECHNICAL, ADMINISTRATIVE, or ASK_MORE_INFO.

            Provide an IssueClassificationResult object with:
            - issueType: "TECHNICAL", "ADMINISTRATIVE", or "ASK_MORE_INFO"
            - technicalIssue: true if the request is technical, false otherwise
            - administrativeIssue: true if the request is administrative, false otherwise
            - message: message to show the user confirming the classification and explaining the next steps
            """)
    Result<IssueClassificationResult> classifyIssue(@V("text") String message,
                                                    @V("customerCode") String customerCode,
                                                    @V("selectedContractNumber") String selectedContractNumber);
}