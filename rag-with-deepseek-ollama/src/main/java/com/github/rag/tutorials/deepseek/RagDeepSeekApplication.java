package com.github.rag.tutorials.deepseek;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Slf4j
public class RagDeepSeekApplication {
    
    public static void main(String[] args) {
        try {
            final DeepSeekR1Context context = new DeepSeekR1Context();
            FileUtils.listFiles(toPath("docs/").toFile(), new String[]{"pdf"}, false)
                    .forEach(file -> {
                        try {
                            Document document = loadDocument(file.toPath(), new ApachePdfBoxDocumentParser());
                            context.ingestDocument(document);
                        } catch (Exception e) {
                            log.error("Error during document ingestion", e);
                        }
                    });
            startConversationWith(context.createAssistant());
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
            URL fileUrl = DeepSeekR1Context.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
