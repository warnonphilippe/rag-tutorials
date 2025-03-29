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