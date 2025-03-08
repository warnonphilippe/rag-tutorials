package com.github.rag.tutorials.helpdesk.application.email;

import com.github.rag.tutorials.helpdesk.domain.conversation.ConversationService;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.MessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponsePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.adapter.email.EmailChannelAdapter;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mail.MailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.mail.internet.MimeMessage;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EmailInputProcessorTest {

    @Mock
    private EmailChannelAdapter mimeEmailAdapter;

    @Mock
    private ConversationService conversationService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message camelMessage;

    @Mock
    private MailMessage mailMessage;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailInputProcessor emailInputProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processEmailSuccessfully() {
        MessagePayload adaptedMessage = MessagePayload.builder().id("1").text("text").build();
        ResponsePayload processedMessage = ResponsePayload.builder().id("1").responseText("response").build();

        when(exchange.getMessage()).thenReturn(camelMessage);
        when(camelMessage.getBody(MailMessage.class)).thenReturn(mailMessage);
        when(mailMessage.getMessage()).thenReturn(mimeMessage);
        when(mimeEmailAdapter.adapt(mimeMessage)).thenReturn(Mono.just(adaptedMessage));
        when(conversationService.processMessage(adaptedMessage)).thenReturn(Mono.just(processedMessage));
        when(mimeEmailAdapter.sendResponse(processedMessage)).thenReturn(Mono.empty());

        emailInputProcessor.process(exchange);

        verify(mimeEmailAdapter).adapt(mimeMessage);
        verify(conversationService).processMessage(adaptedMessage);
        verify(mimeEmailAdapter).sendResponse(processedMessage);
    }

    @Test
    void processEmailWithAdaptationFailure() {
        when(exchange.getMessage()).thenReturn(camelMessage);
        when(camelMessage.getBody(MailMessage.class)).thenReturn(mailMessage);
        when(mailMessage.getMessage()).thenReturn(mimeMessage);
        when(mimeEmailAdapter.adapt(mimeMessage)).thenReturn(Mono.error(new RuntimeException("Adaptation failed")));

        emailInputProcessor.process(exchange);

        verify(mimeEmailAdapter).adapt(mimeMessage);
        verify(conversationService, never()).processMessage(any());
        verify(mimeEmailAdapter, never()).sendResponse(any());
    }

    @Test
    void processEmailWithProcessingFailure() {
        MessagePayload adaptedMessage = MessagePayload.builder().id("1").text("text").build();
        when(exchange.getMessage()).thenReturn(camelMessage);
        when(camelMessage.getBody(MailMessage.class)).thenReturn(mailMessage);
        when(mailMessage.getMessage()).thenReturn(mimeMessage);
        when(mimeEmailAdapter.adapt(mimeMessage)).thenReturn(Mono.just(adaptedMessage));
        when(conversationService.processMessage(adaptedMessage)).thenReturn(Mono.error(new RuntimeException("Processing failed")));

        emailInputProcessor.process(exchange);

        verify(mimeEmailAdapter).adapt(mimeMessage);
        verify(conversationService).processMessage(adaptedMessage);
        verify(mimeEmailAdapter, never()).sendResponse(any());
    }

    @Test
    void processEmailWithResponseFailure() {
        MessagePayload adaptedMessage = MessagePayload.builder().id("1").text("text").build();
        ResponsePayload processedMessage = ResponsePayload.builder().id("1").responseText("response").build();
        when(exchange.getMessage()).thenReturn(camelMessage);
        when(camelMessage.getBody(MailMessage.class)).thenReturn(mailMessage);
        when(mailMessage.getMessage()).thenReturn(mimeMessage);
        when(mimeEmailAdapter.adapt(mimeMessage)).thenReturn(Mono.just(adaptedMessage));
        when(conversationService.processMessage(adaptedMessage)).thenReturn(Mono.just(processedMessage));
        when(mimeEmailAdapter.sendResponse(processedMessage)).thenReturn(Mono.error(new RuntimeException("Response failed")));

        emailInputProcessor.process(exchange);

        verify(mimeEmailAdapter).adapt(mimeMessage);
        verify(conversationService).processMessage(adaptedMessage);
        verify(mimeEmailAdapter).sendResponse(processedMessage);
    }
}