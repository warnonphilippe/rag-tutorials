package com.github.rag.tutorials.helpdesk.domain.security.service;

import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import com.github.rag.tutorials.helpdesk.domain.security.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    public Otp generateOtp(Customer customer) {
        String code = generateRandomCode();
        LocalDateTime now = LocalDateTime.now();

        Otp otp = Otp.builder()
                .customerCode(customer.getCode())
                .code(code)
                .email(customer.getEmail())
                .createdAt(now)
                .expiresAt(now.plusMinutes(15)) // 15 minuti di validità
                .used(false)
                .build();

        return otpRepository.save(otp);
    }

    public boolean verifyOtp(String customerCode, String code) {
        Optional<Otp> otpOpt = otpRepository.findByCustomerCodeAndCodeAndUsedFalseAndExpiresAtAfter(
                customerCode, code, LocalDateTime.now());

        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            otp.setUsed(true);
            otpRepository.save(otp);
            return true;
        }

        return false;
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(800000);
        return String.valueOf(code);
    }
}