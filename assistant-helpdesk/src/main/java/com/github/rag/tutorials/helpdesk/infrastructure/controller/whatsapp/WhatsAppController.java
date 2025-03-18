package com.github.rag.tutorials.helpdesk.infrastructure.controller.whatsapp;

import com.github.rag.tutorials.helpdesk.application.whatsapp.dto.WhatsAppMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppController {
    
    private final ProducerTemplate producerTemplate;
    

    //SmsMessageSid=SMe432421696bfd504b0ea94f2c84626c5
    // &NumMedia=0
    // &ProfileName=Giuseppe+T.
    // &MessageType=text
    // &SmsSid=SMe432421696bfd504b0ea94f2c84626c5
    // &WaId=393490625232
    // &SmsStatus=received
    // &Body=test
    // &To=whatsapp%3A%2B14155238886
    // &NumSegments=1
    // &ReferralNumMedia=0
    // &MessageSid=SMe432421696bfd504b0ea94f2c84626c5
    // &AccountSid=ACbce4252bb35636395a48d0313dc0c831
    // &From=whatsapp%3A%2B393490625232
    // &ApiVersion=2010-04-01
    @PostMapping(value = "/incoming", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Void> incomingMessage(
            @RequestParam("Body") String messageBody,
            @RequestParam("From") String fromNumber,
            @RequestParam("To") String toNumber) {
        log.debug("Received message from WhatsApp: {} from {} to {}", messageBody, fromNumber, toNumber);
        producerTemplate.sendBody("direct:whatsAppInput", new WhatsAppMessageRequest(toNumber, fromNumber, messageBody));
        return ResponseEntity.noContent().build();
    }
}