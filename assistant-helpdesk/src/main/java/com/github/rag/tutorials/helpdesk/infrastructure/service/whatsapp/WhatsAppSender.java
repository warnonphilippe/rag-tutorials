package com.github.rag.tutorials.helpdesk.infrastructure.service.whatsapp;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppSender implements Processor {
    private final String accountSid;
    private final String authToken;

    public WhatsAppSender(@Value("${twilio.account-sid}") String accountSid,
                          @Value("${twilio.auth-token}") String authToken) {
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Sending response to whatsapp: {}", exchange);
        Twilio.init(accountSid, authToken);
        ResponseMessagePayload response = exchange.getMessage().getBody(ResponseMessagePayload.class);
        String from = response.getOriginalMessage().getRecipientId();
        log.debug("From: {}", from);
        String to = response.getOriginalMessage().getSenderId();
        log.debug("To: {}", to);
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                response.getResponseText()
        ).create();
        log.info("Message sent: {}", message.getSid());
    }
}
