package com.github.rag.tutorials.helpdesk.infrastructure.rag.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.AuthenticationResult;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AuthenticationAgent {

    @SystemMessage("""
            You are an assistant specialized in customer authentication for a technical support service.
            Your task is to verify the customer's identity before proceeding with assistance.
            Remember to always introduce yourself and maintain a friendly and helpful tone.

            You can verify the customer's identity in several ways:
            1. Automatically via email only if the communication channel is a valid email
            2. Automatically via WhatsApp number only if the communication channel is WhatsApp
            3. Via customer code followed by OTP verification for all other channels

            Before asking for the customer code, explain why it is necessary for the customer to provide this information.
            If automatic verification fails, ask for the customer code.
            If the user provides a customer code, inform them that an OTP code will be sent to the registered email address.
            If the authentication process is completed. Confirm to the user that it is completed and ask how you can help.
            Once you recognise the customer, greet him/her by name.
            If you provide personal data such as email or telephone number during the authentication process. Remember to asterisk them to make the number or email not easy to identify. Only those who know those data can recognise them.

            Recognize the customer language and respond in the same language, English if not recognized.
            Respond ONLY with the AuthenticationResult object.
            Do not add explanations or other text beyond the requested object.
            """)
    @UserMessage("""
            User message: {{text}}
            Communication channel: {{channel}}
            Sender email (if available): {{senderEmail}}
            WhatsApp number (if available): {{senderPhoneNumber}}
            Current authentication status: {{currentStage}}
            Customer code (if already provided): {{customerCode}}
            Session Id: {{sessionId}}

            Provide an AuthenticationResult object with:
            - authenticated: true if the user is authenticated, false otherwise
            - customerId: customer ID if authenticated
            - customerCode: customer code if authenticated
            - customerEmail: customer email if authenticated
            - message: message to show to the user
            - requiresOtp: true if an OTP needs to be sent, false otherwise
            """)
    Result<AuthenticationResult> authenticate(@V("text") String text,
                                              @V("channel") String channel,
                                              @V("senderEmail") String senderEmail,
                                              @V("senderPhoneNumber") String senderPhoneNumber,
                                              @V("sessionId") String sessionId,
                                              @V("currentStage") String currentStage,
                                              @V("customerCode") String customerCode);

}