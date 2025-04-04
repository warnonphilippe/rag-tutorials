package com.github.rag.tutorials.helpdesk.application.customer;

import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.domain.customer.service.CustomerIdentificationService;
import com.github.rag.tutorials.helpdesk.domain.security.model.Otp;
import com.github.rag.tutorials.helpdesk.domain.security.service.EmailService;
import com.github.rag.tutorials.helpdesk.domain.security.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerAuthenticationService {

    private final CustomerIdentificationService customerIdentificationService;
    private final OtpService otpService;
    private final EmailService emailService;

    public Optional<Customer> authenticateByEmail(String email) {
        return customerIdentificationService.identifyByEmail(email);
    }

    public Optional<Customer> authenticateByWhatsApp(String whatsappNumber) {
        return customerIdentificationService.identifyByWhatsappNumber(whatsappNumber);
    }

    public Customer sendOtpForCustomerCode(String customerCode) {
        Optional<Customer> customerOpt = customerIdentificationService.identifyByCode(customerCode);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            Otp otp = otpService.generateOtp(customer);
            emailService.sendOtpEmail(otp);
            return customer;
        }
        return null;
    }

    public boolean verifyOtp(String customerCode, String otpCode) {
        return otpService.verifyOtp(customerCode, otpCode);
    }
}