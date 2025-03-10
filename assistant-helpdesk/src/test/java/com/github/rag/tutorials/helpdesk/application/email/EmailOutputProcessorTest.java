package com.github.rag.tutorials.helpdesk.application.email;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EmailOutputProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message camelMessage;

    @InjectMocks
    private EmailOutputProcessor emailOutputProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(exchange.getIn()).thenReturn(camelMessage);
    }

    @Test
    void processEmailResponseSuccessfully() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Message-ID", "12345");
        metadata.put("References", "12345");

        RequestMessagePayload originalMessage = RequestMessagePayload.builder()
                .senderEmail("test@example.com")
                .subject("Test Subject")
                .metadata(metadata)
                .build();

        ResponseMessagePayload responsePayload = ResponseMessagePayload.builder()
                .originalMessage(originalMessage)
                .responseText("Response text")
                .build();

        when(camelMessage.getBody(ResponseMessagePayload.class)).thenReturn(responsePayload);

        emailOutputProcessor.process(exchange);

        verify(camelMessage).setHeader("To", "test@example.com");
        verify(camelMessage).setHeader("Subject", "Re: Test Subject");
        verify(camelMessage).setHeader("In-Reply-To", "12345");
        verify(camelMessage).setHeader("References", "12345");
        verify(camelMessage).setHeader(Exchange.CONTENT_TYPE, "text/plain; charset=UTF-8");
        verify(camelMessage).setBody(anyString());
    }

    @Test
    void processEmailResponseWithNullResponsePayload() {
        when(camelMessage.getBody(ResponseMessagePayload.class)).thenReturn(null);

        emailOutputProcessor.process(exchange);

        verify(camelMessage, never()).setHeader(anyString(), anyString());
        verify(camelMessage, never()).setBody(anyString());
    }

    @Test
    void processEmailResponseWithNullOriginalMessage() {
        ResponseMessagePayload responsePayload = ResponseMessagePayload.builder()
                .originalMessage(null)
                .responseText("Response text")
                .build();

        when(camelMessage.getBody(ResponseMessagePayload.class)).thenReturn(responsePayload);

        emailOutputProcessor.process(exchange);

        verify(camelMessage, never()).setHeader(anyString(), anyString());
        verify(camelMessage, never()).setBody(anyString());
    }

    @Test
    void processEmailResponseWithMissingMessageId() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("References", "12345");

        RequestMessagePayload originalMessage = RequestMessagePayload.builder()
                .senderEmail("test@example.com")
                .subject("Test Subject")
                .metadata(metadata)
                .build();

        ResponseMessagePayload responsePayload = ResponseMessagePayload.builder()
                .originalMessage(originalMessage)
                .responseText("Response text")
                .build();

        when(camelMessage.getBody(ResponseMessagePayload.class)).thenReturn(responsePayload);

        emailOutputProcessor.process(exchange);

        verify(camelMessage).setHeader("To", "test@example.com");
        verify(camelMessage).setHeader("Subject", "Re: Test Subject");
        verify(camelMessage, never()).setHeader("In-Reply-To", "12345");
        verify(camelMessage).setHeader("References", "12345");
        verify(camelMessage).setHeader(Exchange.CONTENT_TYPE, "text/plain; charset=UTF-8");
        verify(camelMessage).setBody(anyString());
    }

    @Test
    void processEmailResponseWithEmptyReferences() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Message-ID", "12345");

        RequestMessagePayload originalMessage = RequestMessagePayload.builder()
                .senderEmail("test@example.com")
                .subject("Test Subject")
                .metadata(metadata)
                .build();

        ResponseMessagePayload responsePayload = ResponseMessagePayload.builder()
                .originalMessage(originalMessage)
                .responseText("Response text")
                .build();

        when(camelMessage.getBody(ResponseMessagePayload.class)).thenReturn(responsePayload);

        emailOutputProcessor.process(exchange);

        verify(camelMessage).setHeader("To", "test@example.com");
        verify(camelMessage).setHeader("Subject", "Re: Test Subject");
        verify(camelMessage).setHeader("In-Reply-To", "12345");
        verify(camelMessage).setHeader("References", "12345");
        verify(camelMessage).setHeader(Exchange.CONTENT_TYPE, "text/plain; charset=UTF-8");
        verify(camelMessage).setBody(anyString());
    }
}