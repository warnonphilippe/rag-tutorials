package com.github.rag.tutorials.helpdesk;

import com.github.rag.tutorials.helpdesk.domain.contract.model.Contract;
import com.github.rag.tutorials.helpdesk.domain.contract.repository.ContractRepository;
import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.domain.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initData(CustomerRepository customerRepository, ContractRepository contractRepository) {
        return args -> {
            // Crea e salva i clienti
            Customer customer1 = Customer.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .code("CUST123")
                    .email("john.doe@gmail.com")
                    .whatsappNumber("0987654321")
                    .build();
            
            Customer customer2 = Customer.builder().firstName("Jane")
                    .lastName("Doe")
                    .code("CUST456")
                    .email("jane.doe@gmail.com")
                    .whatsappNumber("0987654321")
                    .build();
            
            customerRepository.save(customer1);
            customerRepository.save(customer2);

            // Crea e salva i contratti
            Contract contract1 = Contract.builder()
                    .customer(customer1)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1))
                    .build();
            contractRepository.save(contract1);
        };
    }
}
