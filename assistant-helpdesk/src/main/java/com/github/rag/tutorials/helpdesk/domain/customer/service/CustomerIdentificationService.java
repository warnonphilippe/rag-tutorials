package com.github.rag.tutorials.helpdesk.domain.customer.service;

import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerIdentificationService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> identifyByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> identifyByWhatsappNumber(String whatsappNumber) {
        return customerRepository.findByWhatsappNumber(whatsappNumber);
    }

    public Optional<Customer> identifyByCode(String customerCode) {
        return customerRepository.findByCode(customerCode);
    }
}