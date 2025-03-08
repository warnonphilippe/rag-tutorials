package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "conversation")
@Getter
@Setter
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String customerId;
    @Column(columnDefinition = "TEXT")
    private String messages;

    public Conversation(String customerId,
                        String messages) {
        this.customerId = customerId;
        this.messages = messages;
    }
}
