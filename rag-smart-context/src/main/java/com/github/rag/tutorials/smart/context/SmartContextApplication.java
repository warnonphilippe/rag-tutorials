package com.github.rag.tutorials.smart.context;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Slf4j
public class SmartContextApplication {

    public static void main(String[] args) {
        try {
            final SmartContextManager manager = new SmartContextManager();
            Document document = loadDocument(toPath("docs/91P6LXArGaS.pdf"), new ApachePdfBoxDocumentParser());
            manager.ingestDocument(document);
            startConversationWith(manager.createAssistant());
        } catch (Exception e) {
            log.error("Error during application execution", e);
        }
    }

    public static void startConversationWith(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                log.info("==================================================");
                log.info("User: ");
                String userQuery = scanner.nextLine();
                log.info("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = assistant.chat(userQuery);
                log.info("==================================================");
                log.info("Assistant: " + agentAnswer);
            }
        }
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = SmartContextApplication.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}