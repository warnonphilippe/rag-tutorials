package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.KnowledgeBaseResult;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface KnowledgeBaseAgent {

    @SystemMessage("""
            You are an assistant specialized in searching for solutions in the knowledge base.
            Your task is to search for solutions to technical problems in the company's knowledge base.
            
            The knowledge base contains articles, guides, and FAQs that can help solve common problems.
            You need to search for relevant solutions to the problem described by the customer and, if found, present them clearly and concisely.
            If you do not find adequate solutions, you must indicate this so that the request can be forwarded to a human operator.
            
            Respond ONLY with solution found in your knowledge base.
            Recognize the customer language and respond in the same language, English if not recognized.
            Respond ONLY with the KnowledgeBaseResult object.
            Do not add explanations or any other text beyond the requested object.
            """)
    @UserMessage("""
            User message: {{text}}
            Customer code: {{customerCode}}
            Selected contract ID: {{selectedContractNumber}}
            Issue type: {{issueType}}
            
            Provide a KnowledgeBaseResult object with:
            - solutionFound: true if a solution was found, false otherwise
            - solution: description of the solution found (if present)
            - message: message to show to the user with the solution or indication that a support ticket will be created
            """)
    Result<KnowledgeBaseResult> searchKnowledgeBase(@V("text") String message,
                                                    @V("customerCode") String customerCode,
                                                    @V("selectedContractNumber") String selectedContractNumber,
                                                    @V("issueType") String issueType);

}