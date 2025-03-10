package com.github.rag.tutorials.helpdesk.infrastructure.adapter.email;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailChannelAdapterTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageParser mimeMessageParser;

    @InjectMocks
    private EmailChannelAdapter emailChannelAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void adaptEmailSuccessfully() throws Exception {
        // Create a real MimeMessage
        jakarta.mail.Session session = jakarta.mail.Session.getDefaultInstance(new Properties());
        MimeMessage realMessage = new MimeMessage(session);
        realMessage.setSubject("Subject");
        realMessage.setFrom(new InternetAddress("sender@example.com"));
        realMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress("recipient@example.com"));
        realMessage.setText("Email content");
        realMessage.saveChanges();

        // Use the real message in our test
        Mono<RequestMessagePayload> result = emailChannelAdapter.adapt(realMessage);

        StepVerifier.create(result)
                .expectNextMatches(payload ->
                        payload.getText().equals("Email content") &&
                                payload.getSenderId().equals("sender@example.com") &&
                                payload.getRecipientId().equals("recipient@example.com") &&
                                payload.getMetadata().get("subject").equals("Subject") &&
                                payload.getMetadata().containsKey("messageId"))
                .verifyComplete();
    }

    @Test
    void adaptEmailWithAttachments() throws Exception {
        // Create a real MimeMessage with attachment
        jakarta.mail.Session session = jakarta.mail.Session.getDefaultInstance(new Properties());
        MimeMessage realMessage = new MimeMessage(session);
        realMessage.setSubject("Subject");
        realMessage.setFrom(new InternetAddress("sender@example.com"));
        realMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress("recipient@example.com"));

        // Create a multipart message with attachment
        jakarta.mail.internet.MimeMultipart multipart = new jakarta.mail.internet.MimeMultipart();
        jakarta.mail.internet.MimeBodyPart textPart = new jakarta.mail.internet.MimeBodyPart();
        textPart.setText("Email content");
        multipart.addBodyPart(textPart);

        // Add attachment
        jakarta.mail.internet.MimeBodyPart attachmentPart = new jakarta.mail.internet.MimeBodyPart();
        jakarta.activation.DataSource dataSource = new jakarta.activation.DataSource() {
            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                return new java.io.ByteArrayInputStream("test data".getBytes());
            }

            @Override
            public java.io.OutputStream getOutputStream() throws java.io.IOException {
                return new java.io.ByteArrayOutputStream();
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public String getName() {
                return "test.txt";
            }
        };
        attachmentPart.setDataHandler(new jakarta.activation.DataHandler(dataSource));
        attachmentPart.setFileName("test.txt");
        multipart.addBodyPart(attachmentPart);

        realMessage.setContent(multipart);
        realMessage.saveChanges();

        // Use the real message in our test
        Mono<RequestMessagePayload> result = emailChannelAdapter.adapt(realMessage);

        StepVerifier.create(result)
                .expectNextMatches(payload ->
                        payload.getText().equals("Email content") &&
                                payload.getSenderId().equals("sender@example.com") &&
                                payload.getRecipientId().equals("recipient@example.com") &&
                                payload.getMetadata().get("subject").equals("Subject") &&
                                payload.getMetadata().containsKey("messageId") &&
                                payload.getMetadata().get("hasAttachments").equals(true) &&
                                payload.getMetadata().get("attachmentCount").equals(1) &&
                                payload.getAttachments().size() == 1 &&
                                payload.getAttachments().containsKey("test.txt"))
                .verifyComplete();
    }

    @Test
    void adaptEmailWithParsingError() throws Exception {
        when(mimeMessageParser.parse()).thenThrow(new RuntimeException("Parsing error"));

        Mono<RequestMessagePayload> result = emailChannelAdapter.adapt(mimeMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Failed to parse email"))
                .verify();
    }

    @Test
    void sendResponseSuccessfully() {
        ResponseMessagePayload responsePayload = ResponseMessagePayload.builder().responseText("Response text").build();

        Mono<Void> result = emailChannelAdapter.sendResponse(responsePayload);

        StepVerifier.create(result)
                .verifyComplete();

        verify(producerTemplate).sendBody("direct:sendEmail", responsePayload);
    }

    @Test
    void getChannelNameReturnsEmail() {
        assertEquals("email", emailChannelAdapter.getChannelName());
    }
}