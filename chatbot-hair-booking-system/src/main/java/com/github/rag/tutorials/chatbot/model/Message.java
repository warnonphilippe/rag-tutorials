package com.github.rag.tutorials.chatbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "message")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String memoryId;
    @Column(columnDefinition = "TEXT")
    private String messages;

    public Message(String memoryId, String messages) {
        this.memoryId = memoryId;
        this.messages = messages;
    }
}
