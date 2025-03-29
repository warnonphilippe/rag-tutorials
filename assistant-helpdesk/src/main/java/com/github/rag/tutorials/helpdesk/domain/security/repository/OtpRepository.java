package com.github.rag.tutorials.helpdesk.domain.security.repository;

import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import com.github.rag.tutorials.helpdesk.infrastructure.repository.security.JpaOtpRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaOtpRepository {
    Optional<Otp> findByCustomerCodeAndCodeAndUsedFalseAndExpiresAtAfter(String customerCode,
                                                                         String code,
                                                                         LocalDateTime now);
}
