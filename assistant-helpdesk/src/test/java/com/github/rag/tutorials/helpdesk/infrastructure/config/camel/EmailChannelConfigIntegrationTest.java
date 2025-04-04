package com.github.rag.tutorials.helpdesk.infrastructure.config.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EmailChannelConfigIntegrationTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:bean:emailInputProcessor")
    private MockEndpoint mockEmailInputProcessor;

    @EndpointInject("mock:smtpEndpoint")
    private MockEndpoint mockSmtpEndpoint;

    @BeforeEach
    public void setUp() throws Exception {
        // Configurazione per il test della rotta incomingEmail
        AdviceWith.adviceWith(camelContext, "incomingEmail", route -> {
            // Sostituzione dell'endpoint IMAP con un mock consumer endpoint
            route.replaceFromWith("direct:mockImap");
            // Sostituiamo il bean processor per testarlo
            route.weaveByToUri("bean:emailInputProcessor").replace().to("mock:bean:emailInputProcessor");
        });

        // Configurazione per il test della rotta outgoingEmail
        AdviceWith.adviceWith(camelContext, "outgoingEmail", route -> {
            // Sostituiamo l'endpoint SMTP con un mock
            route.weaveByToUri("smtp:*").replace().to("mock:smtpEndpoint");
        });

        camelContext.start();
    }

    @Test
    public void testIncomingEmailRoute() throws Exception {
        // Configuriamo le aspettative per il mock
        mockEmailInputProcessor.expectedMessageCount(1);
        mockEmailInputProcessor.expectedHeaderReceived("From", "sender@example.com");
        mockEmailInputProcessor.expectedHeaderReceived("Subject", "Test Subject");

        // Creiamo un messaggio email di test
        Map<String, Object> headers = new HashMap<>();
        headers.put("From", "sender@example.com");
        headers.put("Subject", "Test Subject");

        // Inviamo il messaggio di test all'endpoint mockato
        producerTemplate.sendBodyAndHeaders("direct:mockImap", "Test email body", headers);

        // Verifichiamo che il mock abbia ricevuto il messaggio con gli header attesi
        mockEmailInputProcessor.assertIsSatisfied();
    }

    @Test
    public void testOutgoingEmailRoute() throws Exception {
        // Configuriamo le aspettative per il mock SMTP
        mockSmtpEndpoint.expectedMessageCount(1);
        mockSmtpEndpoint.expectedHeaderReceived("To", "recipient@example.com");
        mockSmtpEndpoint.expectedHeaderReceived("From", "support@test.com");
        mockSmtpEndpoint.expectedHeaderReceived("Subject", "Response Subject");

        // Creiamo un messaggio email di test
        Map<String, Object> headers = new HashMap<>();
        headers.put("To", "recipient@example.com");
        headers.put("Subject", "Response Subject");

        // Inviamo il messaggio di test all'endpoint direct:sendEmail
        producerTemplate.sendBodyAndHeaders("direct:sendEmail", "Response email body", headers);

        // Verifichiamo che il mock SMTP abbia ricevuto il messaggio con gli header attesi
        mockSmtpEndpoint.assertIsSatisfied();
    }
}
