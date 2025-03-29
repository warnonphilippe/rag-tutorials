package com.github.rag.tutorials.helpdesk.infrastructure.repository.customer;

import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface JpaCustomerRepository extends CrudRepository<Customer, UUID> {
}
