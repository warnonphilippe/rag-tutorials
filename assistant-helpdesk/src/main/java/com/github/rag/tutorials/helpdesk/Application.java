package com.github.rag.tutorials.helpdesk;

import com.github.rag.tutorials.helpdesk.domain.contract.model.Contract;
import com.github.rag.tutorials.helpdesk.domain.contract.repository.ContractRepository;
import com.github.rag.tutorials.helpdesk.domain.customer.model.Customer;
import com.github.rag.tutorials.helpdesk.domain.customer.repository.CustomerRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initData(CustomerRepository customerRepository, 
                               ContractRepository contractRepository,
                               EmbeddingStoreIngestor ingestor,
                               ResourceLoader resourceLoader,
                               Tokenizer tokenizer) {
        return args -> {
            List<String> documents = Arrays.asList("guida_smartpos.pdf", "guida-utilizzo-mobilepos.pdf");
            for (String document : documents) {
                Resource resource = resourceLoader.getResource("classpath:" +document);
                Document doc = loadDocument(resource.getFile().toPath(), new ApachePdfBoxDocumentParser());
                ingestor.ingest(doc);
            }
            
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

            Contract contract1 = Contract.builder()
                    .customer(customer1)
                    .active(true)
                    .contractType("FULL")
                    .contractNumber("CONTRACT123")
                    .description("Full contract for John Doe")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1))
                    .build();
            contractRepository.save(contract1);
        };
    }
}
