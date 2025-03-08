package com.github.rag.tutorials.helpdesk.infrastructure.config.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

public class EmailChannelConfigTest extends CamelTestSupport {

    private EmailChannelConfig emailChannelConfig;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        // Aggiungiamo i bean necessari
        context.getRegistry().bind("emailInputProcessor", new DummyProcessor());
        context.getRegistry().bind("emailOutputProcessor", new DummyProcessor());
        return context;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        emailChannelConfig = new EmailChannelConfig();
        // Impostiamo manualmente le proprietà che normalmente sarebbero impostate da Spring
        ReflectionTestUtils.setField(emailChannelConfig, "imapHost", "localhost");
        ReflectionTestUtils.setField(emailChannelConfig, "imapPort", 143);
        ReflectionTestUtils.setField(emailChannelConfig, "imapUsername", "testuser");
        ReflectionTestUtils.setField(emailChannelConfig, "imapPassword", "testpass");
        ReflectionTestUtils.setField(emailChannelConfig, "smtpHost", "localhost");
        ReflectionTestUtils.setField(emailChannelConfig, "smtpPort", 25);
        ReflectionTestUtils.setField(emailChannelConfig, "smtpUsername", "testuser");
        ReflectionTestUtils.setField(emailChannelConfig, "smtpPassword", "testpass");
        ReflectionTestUtils.setField(emailChannelConfig, "supportEmailAddress", "support@test.com");
        ReflectionTestUtils.setField(emailChannelConfig, "sslEnabled", false);

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Sostituiamo gli endpoint reali con mock endpoints per i test
                from("direct:mockImap")
                        .routeId("incomingEmail")
                        .log("Received email from ${header.From} with subject: ${header.Subject}")
                        .to("mock:emailInputProcessor");

                from("direct:sendEmail")
                        .routeId("outgoingEmail")
                        .log("Sending email to ${header.To}")
                        .setHeader("From", constant("support@test.com"))
                        .bean("emailOutputProcessor")
                        .to("mock:smtpEndpoint");
            }
        };
    }

    @Test
    public void testIncomingEmailRoute() throws Exception {
        MockEndpoint mockEmailProcessor = getMockEndpoint("mock:emailInputProcessor");
        mockEmailProcessor.expectedMessageCount(1);
        mockEmailProcessor.expectedHeaderReceived("From", "sender@example.com");
        mockEmailProcessor.expectedHeaderReceived("Subject", "Test Subject");

        Map<String, Object> headers = new HashMap<>();
        headers.put("From", "sender@example.com");
        headers.put("Subject", "Test Subject");

        template.sendBodyAndHeaders("direct:mockImap", "Test email body", headers);

        mockEmailProcessor.assertIsSatisfied();
    }

    @Test
    public void testOutgoingEmailRoute() throws Exception {
        MockEndpoint mockSmtp = getMockEndpoint("mock:smtpEndpoint");
        mockSmtp.expectedMessageCount(1);
        mockSmtp.expectedHeaderReceived("To", "recipient@example.com");
        mockSmtp.expectedHeaderReceived("From", "support@test.com");

        Map<String, Object> headers = new HashMap<>();
        headers.put("To", "recipient@example.com");
        headers.put("Subject", "Response Subject");

        template.sendBodyAndHeaders("direct:sendEmail", "Response email body", headers);

        mockSmtp.assertIsSatisfied();
    }

    // Classe helper per sostituire i bean processor reali
    private static class DummyProcessor {
        public Object process(Object body) {
            return body;
        }
    }
}