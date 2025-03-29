package com.github.rag.tutorials.helpdesk.domain.conversation.service;

import com.github.rag.tutorials.helpdesk.domain.conversation.model.Channel;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.CustomerSession;
import com.github.rag.tutorials.helpdesk.domain.conversation.repository.CustomerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSessionService {

    private final CustomerSessionRepository sessionRepository;

    public CustomerSession createSession(Channel channel) {
        CustomerSession session = CustomerSession.builder()
                .id(UUID.randomUUID())
                .channel(channel)
                .authenticated(false)
                .authenticationStatus(CustomerSession.AuthenticationStatus.UNAUTHENTICATED)
                .createdAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();

        return sessionRepository.save(session);
    }

    public Optional<CustomerSession> getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public CustomerSession updateSession(CustomerSession session) {
        session.setLastActivityAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public CustomerSession authenticateSession(CustomerSession session, String customerCode) {
        session.setCustomerCode(customerCode);
        session.setAuthenticated(true);
        session.setAuthenticationStatus(CustomerSession.AuthenticationStatus.AUTHENTICATED);
        return updateSession(session);
    }

    public CustomerSession setSessionPendingOtp(CustomerSession session, String customerCode) {
        session.setCustomerCode(customerCode);
        session.setAuthenticated(false);
        session.setAuthenticationStatus(CustomerSession.AuthenticationStatus.PENDING_OTP);
        return updateSession(session);
    }

    public CustomerSession setSessionPendingCustomerId(CustomerSession session) {
        session.setAuthenticated(false);
        session.setAuthenticationStatus(CustomerSession.AuthenticationStatus.PENDING_CUSTOMER_CODE);
        return updateSession(session);
    }
}