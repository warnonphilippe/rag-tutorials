package com.github.rag.tutorials.helpdesk.domain.security.service;

import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final ProducerTemplate producerTemplate;

    public void sendOtpEmail(Otp otp) {
        String subject = "Verification code for assistance";
        log.info("Sending email to: {}", otp.getEmail());
        log.info("Email subject: {}", subject);
        log.debug("OTP code: {}", otp.getCode());
 
        String body = String.format(
                "Dear customer,\n\n" +
                        "your verification code is: %s\n\n" +
                        "The code is valid for 15 minutes.\n\n" +
                        "Best regards,\n" +
                        "The support service", otp.getCode());

        Map<String, Object> headers = new HashMap<>();
        headers.put("To", otp.getEmail());
        headers.put("Subject", subject);

        //producerTemplate.sendBodyAndHeaders("direct:sendEmail", body, headers);
    }
}