package com.github.rag.tutorials.helpdesk.application.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WhatsAppMessageRequest {
    private String toNumber;
    private String fromNumber;
    private String messageBody;
}
