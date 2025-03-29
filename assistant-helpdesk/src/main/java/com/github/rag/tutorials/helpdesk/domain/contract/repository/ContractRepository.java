package com.github.rag.tutorials.helpdesk.domain.contract.repository;

import com.github.rag.tutorials.helpdesk.domain.contract.model.Contract;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.contract.JpaContractRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaContractRepository {
    List<Contract> findByCustomerId(UUID customerId);
    List<Contract> findByCustomerIdAndActiveTrue(UUID customerId);
}
