package com.github.rag.tutorials.helpdesk.domain.security.service;

import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ProducerTemplate producerTemplate;

    public void sendOtpEmail(Otp otp) {
        String subject = "Verification code for assistance";
        String body = String.format(
                "Dear customer,\n\n" +
                        "your verification code is: %s\n\n" +
                        "The code is valid for 15 minutes.\n\n" +
                        "Best regards,\n" +
                        "The support service", otp.getCode());

        Map<String, Object> headers = new HashMap<>();
        headers.put("To", otp.getEmail());
        headers.put("Subject", subject);

        producerTemplate.sendBodyAndHeaders("direct:sendEmail", body, headers);
    }
}