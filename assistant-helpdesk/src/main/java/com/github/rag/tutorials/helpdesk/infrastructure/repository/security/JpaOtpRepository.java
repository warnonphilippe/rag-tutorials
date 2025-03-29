package com.github.rag.tutorials.helpdesk.infrastructure.repository.security;

import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface JpaOtpRepository extends CrudRepository<Otp, UUID> {
}
