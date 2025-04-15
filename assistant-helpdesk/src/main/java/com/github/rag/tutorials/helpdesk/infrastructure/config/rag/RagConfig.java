package com.github.rag.tutorials.helpdesk.infrastructure.config.rag;

import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.*;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.store.JpaConversationMemoryStore;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.tool.CustomerIdentificationTool;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.tool.TicketCreationTool;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Bean
    Tokenizer tokenizer() {
        return new HuggingFaceTokenizer();
    }

    @Bean
    ChatMemory chatMemory(JpaConversationMemoryStore jpaConversationMemoryStore) {
        return MessageWindowChatMemory.builder()
                .chatMemoryStore(jpaConversationMemoryStore)
                .maxMessages(15)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingStore<TextSegment> embeddingStore,
                                                  Tokenizer tokenizer,
                                                  EmbeddingModel embeddingModel) {
        DocumentSplitter documentSplitter = new DocumentBySentenceSplitter(250, 50, tokenizer);
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        int maxResults = 5;
        double minScore = 0.6;
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }


    @Bean
    AuthenticationAgent authenticationAgent(ChatLanguageModel model,
                                            ChatMemory chatMemoryProvider,
                                            CustomerIdentificationTool customerIdentificationTool) {
        return AiServices.builder(AuthenticationAgent.class)
                .chatLanguageModel(model)
                .tools(customerIdentificationTool)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    ContractVerificationAgent contractVerificationAgent(ChatLanguageModel model,
                                                        ChatMemory chatMemoryProvider) {
        return AiServices.builder(ContractVerificationAgent.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    IssueClassificationAgent issueClassificationAgent(ChatLanguageModel model,
                                                      ChatMemory chatMemoryProvider) {
        return AiServices.builder(IssueClassificationAgent.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    KnowledgeBaseAgent knowledgeBaseAgent(ChatLanguageModel model,
                                          RetrievalAugmentor retrievalAugmentor,
                                          ChatMemory chatMemoryProvider) {
        return AiServices.builder(KnowledgeBaseAgent.class)
                .chatLanguageModel(model)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    TicketCreationAgent ticketCreationAgent(ChatLanguageModel model,
                                            ChatMemory chatMemoryProvider,
                                            TicketCreationTool ticketCreationTool) {

        return AiServices.builder(TicketCreationAgent.class)
                .chatLanguageModel(model)
                .tools(ticketCreationTool)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    RetrievalAugmentor retrievalAugmentor(ContentRetriever contentRetriever,
                                          ContentInjector contentInjector) {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();
    }

    @Bean
    StatefulSupervisorAgent statefulSupervisorAgent(ChatLanguageModel model,
                                                    ChatMemory chatMemoryProvider) {
        return AiServices.builder(StatefulSupervisorAgent.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemoryProvider)
                .build();
    }

    @Bean
    ContentInjector contentInjector() {
        return DefaultContentInjector.builder()
                .metadataKeysToInclude(java.util.Arrays.asList("source", "relevance", "language"))
                .build();
    }
}
