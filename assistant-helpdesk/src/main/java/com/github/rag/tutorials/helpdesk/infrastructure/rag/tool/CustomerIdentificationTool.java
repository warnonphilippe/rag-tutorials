package com.github.rag.tutorials.helpdesk.infrastructure.rag.tool;

import com.github.rag.tutorials.helpdesk.application.customer.CustomerAuthenticationService;
import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerIdentificationTool {

    private final CustomerAuthenticationService customerAuthenticationService;

    @Tool("Identify a customer by email")
    public CustomerIdentificationResult identifyCustomerByEmail(String email) {
        Optional<Customer> customerOpt = customerAuthenticationService.authenticateByEmail(email);
        return createResult(customerOpt);
    }

    @Tool("Identify a customer by WhatsApp number")
    public CustomerIdentificationResult identifyCustomerByWhatsApp(String whatsappNumber) {
        Optional<Customer> customerOpt = customerAuthenticationService.authenticateByWhatsApp(whatsappNumber);
        return createResult(customerOpt);
    }

    @Tool("Send an OTP code via email to the customer")
    public boolean sendOtpToCustomer(String customerCode) {
        return customerAuthenticationService.sendOtpForCustomerCode(customerCode);
    }

    @Tool("Verify an OTP code entered by the customer")
    public boolean verifyCustomerOtp(String customerCode, String otpCode) {
        return customerAuthenticationService.verifyOtp(customerCode, otpCode);
    }

    private CustomerIdentificationResult createResult(Optional<Customer> customerOpt) {
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return new CustomerIdentificationResult(
                    true,
                    customer.getId(),
                    customer.getCode(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail()
            );
        }

        return new CustomerIdentificationResult(false, null, null, null, null, null);
    }

    public record CustomerIdentificationResult(
            boolean customerFound,
            UUID customerId,
            String code,
            String firstName,
            String lastName,
            String email
    ) {}
}