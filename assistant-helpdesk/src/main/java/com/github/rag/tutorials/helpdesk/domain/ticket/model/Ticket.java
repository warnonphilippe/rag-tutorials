package com.github.rag.tutorials.helpdesk.domain.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(nullable = false)
    private String customerId;
    @Column(nullable = false)
    private String contractNumber;
    @Column(nullable = false)
    private String issueType;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String priority;
}
