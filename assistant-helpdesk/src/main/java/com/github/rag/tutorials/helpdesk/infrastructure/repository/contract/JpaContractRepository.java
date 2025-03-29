package com.github.rag.tutorials.helpdesk.infrastructure.repository.contract;

import com.github.rag.tutorials.helpdesk.domain.contract.model.Contract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface JpaContractRepository extends CrudRepository<Contract, UUID> {
}
