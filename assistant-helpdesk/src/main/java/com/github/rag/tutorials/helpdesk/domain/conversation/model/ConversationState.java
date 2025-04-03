package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "conversation_states")
public class ConversationState {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Stage currentStage;

    private UUID customerId;
    private String customerCode;
    private String customerEmail;
    private String selectedContractNumber;
    private String issueType;
    private UUID ticketId;
    private String completionReason;
    private LocalDateTime lastUpdated = LocalDateTime.now();
    private Integer retryCount = 0;

    @Column(columnDefinition = "TEXT")
    private String additionalDataJson;

    @Transient
    private Map<String, Object> additionalData = new HashMap<>();

    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

    public void clearData() {
        this.customerId = null;
        this.customerCode = null;
        this.customerEmail = null;
        this.selectedContractNumber = null;
        this.issueType = null;
        this.ticketId = null;
        this.completionReason = null;
        this.additionalData.clear();
        this.additionalDataJson = "{}";
    }

    public void putData(String key, Object value) {
        additionalData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) additionalData.get(key);
    }

    // Metodi per la conversione tra Map e JSON
    @PrePersist
    @PreUpdate
    public void beforeSave() {
        // Utilizzo di Jackson o Gson per convertire la mappa in JSON
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.additionalDataJson = mapper.writeValueAsString(this.additionalData);
        } catch (Exception e) {
            this.additionalDataJson = "{}";
        }
    }

    @PostLoad
    public void afterLoad() {
        // Utilizzo di Jackson o Gson per convertire il JSON in mappa
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (this.additionalDataJson != null && !this.additionalDataJson.isEmpty()) {
                this.additionalData = mapper.readValue(this.additionalDataJson,
                        new com.fasterxml.jackson.core.type.TypeReference<HashMap<String, Object>>() {});
            }
        } catch (Exception e) {
            this.additionalData = new HashMap<>();
        }
    }
}