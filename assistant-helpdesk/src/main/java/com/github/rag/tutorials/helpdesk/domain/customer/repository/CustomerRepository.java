package com.github.rag.tutorials.helpdesk.domain.customer.repository;


import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.customer.JpaCustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaCustomerRepository {
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByWhatsappNumber(String whatsappNumber);

    Optional<Customer> findByCode(String customerCode);
}
