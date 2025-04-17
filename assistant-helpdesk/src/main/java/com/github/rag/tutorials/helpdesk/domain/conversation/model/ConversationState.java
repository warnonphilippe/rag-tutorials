package com.github.rag.tutorials.helpdesk.domain.conversation.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rag.tutorials.helpdesk.application.agent.dto.IssueType;
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

    @Enumerated(EnumType.STRING)
    private IssueType issueType;
    private UUID ticketId;
    
    @Enumerated(EnumType.STRING)
    private CompletionReason completionReason;
    
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String additionalDataJson;

    @Transient
    private Map<String, Object> additionalData = new HashMap<>();

    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

    public void clearData() {
        this.customerId = null;
        this.customerCode = "";
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

    @PrePersist
    @PreUpdate
    public void beforeSave() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.additionalDataJson = mapper.writeValueAsString(this.additionalData);
            updateLastUpdated();
        } catch (Exception e) {
            this.additionalDataJson = "{}";
        }
    }

    @PostLoad
    public void afterLoad() {
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