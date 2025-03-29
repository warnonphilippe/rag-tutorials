package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.CustomerSession;
import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AuthenticationAgent {

    @SystemMessage("""
            You are a customer support assistant who must verify the customer's identity before providing assistance.
            To authenticate a customer, you can use:
            1. Email
            2. Phone number
            3. WhatsApp number
            4. Customer code followed by OTP verification
            
            If the customer provides an email, phone number, or WhatsApp number, try to identify them automatically.
            If automatic identification fails, ask for the customer code and then send an OTP via email.
            """)
    @UserMessage("""
            Customer authentication status: {{session.authenticationStatus}}
            User message: {{userMessage}}
            """)
    Result<String> authenticateCustomer(@MemoryId String sessionId,
                                        @V("session") CustomerSession session,
                                        @V("userMessage") String userMessage);
}