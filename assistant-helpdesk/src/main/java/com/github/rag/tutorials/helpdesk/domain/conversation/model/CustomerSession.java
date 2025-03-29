package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String customerCode;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private boolean authenticated;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;

    @Enumerated(EnumType.STRING)
    private AuthenticationStatus authenticationStatus;

    public enum AuthenticationStatus {
        AUTHENTICATED,
        PENDING_OTP,
        PENDING_CUSTOMER_CODE,
        UNAUTHENTICATED
    }
}
