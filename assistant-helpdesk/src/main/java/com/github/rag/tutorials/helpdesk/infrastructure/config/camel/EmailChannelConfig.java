package com.github.rag.tutorials.helpdesk.infrastructure.config.camel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
@Setter
public class EmailChannelConfig {

    @Value("${mail.imap.host}")
    private String imapHost;

    @Value("${mail.imap.port}")
    private int imapPort;

    @Value("${mail.imap.username}")
    private String imapUsername;

    @Value("${mail.imap.password}")
    private String imapPassword;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private int smtpPort;

    @Value("${mail.smtp.username}")
    private String smtpUsername;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${mail.support.address}")
    private String supportEmailAddress;

    @Value("${mail.ssl.enabled}")
    private boolean sslEnabled;

    @Value("${mail.imap.disabled}")
    private boolean imapDisabled;

    @Bean
    public RouteBuilder mailRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() {

                if (imapDisabled) {
                    log.info("IMAP is disabled, skipping email configuration");
                    return;
                }

                String imapProtocol = sslEnabled ? "imaps" : "imap";
                String smtpProtocol = sslEnabled ? "smtps" : "smtp";

                String imapEndpoint = String.format(
                        "%s://%s:%d?username=%s&password=%s&delete=false&unseen=true",
                        imapProtocol, imapHost, imapPort, imapUsername, imapPassword
                );

                String smtpEndpoint = String.format(
                        "%s://%s:%d?username=%s&password=%s",
                        smtpProtocol, smtpHost, smtpPort, smtpUsername, smtpPassword
                );

                from(imapEndpoint)
                        .routeId("incomingEmail")
                        .log("Received email from ${header.From} with subject: ${header.Subject}")
                        .to("bean:emailInputProcessor");

                from("direct:sendEmail")
                        .routeId("outgoingEmail")
                        .log("Sending email to ${header.To}")
                        .setHeader("From", constant(supportEmailAddress))
                        .bean("emailOutputProcessor")
                        .to(smtpEndpoint);
            }
        };
    }
}
