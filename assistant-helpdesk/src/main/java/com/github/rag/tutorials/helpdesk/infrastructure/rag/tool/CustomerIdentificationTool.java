package com.github.rag.tutorials.helpdesk.infrastructure.rag.tool;

import com.github.rag.tutorials.helpdesk.application.customer.CustomerAuthenticationService;
import com.github.rag.tutorials.helpdesk.application.customer.dto.CustomerIdentificationResult;
import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerIdentificationTool {

    private final CustomerAuthenticationService customerAuthenticationService;

    @Tool("Identify a customer by email. This tool can be used only for the email channel")
    public CustomerIdentificationResult identifyCustomerByEmail(
            @P("Customer's email address") String email) {
        Optional<Customer> customerOpt = customerAuthenticationService.authenticateByEmail(email);
        return createResult(customerOpt);
    }

    @Tool("Identify a customer by WhatsApp number. This tool can be used only for the WhatsApp channel")
    public CustomerIdentificationResult identifyCustomerByWhatsApp(
            @P("Customer's WhatsApp number") String whatsappNumber) {
        Optional<Customer> customerOpt = customerAuthenticationService.authenticateByWhatsApp(whatsappNumber);
        return createResult(customerOpt);
    }

    @Tool("Send an OTP code via email to the customer. " +
          "This tool is used to send an OTP when the system is unable to automatically recognize the customer. " +
          "The OTP is sent to the email address associated with the customer. " +
          "Return the customer object if the OTP is sent successfully. ")
    public CustomerIdentificationResult sendOtpToCustomer(
            @P("Customer code entered during the authentication phase") String customerCode) {
        Customer customer = customerAuthenticationService.sendOtpForCustomerCode(customerCode);
        return createResult(Optional.ofNullable(customer));
    }

    @Tool("Verify an OTP code entered by the customer. " +
          "This tool is used to verify the OTP entered by the customer. " +
          "Return true if the OTP is valid, false otherwise.")
    public boolean verifyCustomerOtp(@P("Customer code entered during the authentication phase") String customerCode,
                                     @P("OTP code sent via email") String otpCode) {
        return customerAuthenticationService.verifyOtp(customerCode, otpCode);
    }

    private CustomerIdentificationResult createResult(Optional<Customer> customerOpt) {
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return CustomerIdentificationResult.builder()
                    .customerFound(true)
                    .customerId(customer.getId())
                    .customerCode(customer.getCode())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .build();
        }
        return CustomerIdentificationResult.builder().build();
    }
}