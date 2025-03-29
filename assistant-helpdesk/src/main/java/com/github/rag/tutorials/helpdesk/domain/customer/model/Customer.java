package com.github.rag.tutorials.helpdesk.domain.customer.model;

import com.github.rag.tutorials.helpdesk.domain.contract.model.Contract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(unique = true, nullable = false)
    private String code;
    private String email;
    private String whatsappNumber;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "customer")
    private List<Contract> contracts;
}
