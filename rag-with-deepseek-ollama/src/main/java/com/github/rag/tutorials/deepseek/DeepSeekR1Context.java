package com.github.rag.tutorials.deepseek;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
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
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class DeepSeekR1Context {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final RetrievalAugmentor retrievalAugmentor;
    private final MessageWindowChatMemory chatMemory;

    /**
     * Initializes the context management system by configuring:
     * - Chat model for interaction with the LLM
     * - Embedding model for semantic text encoding
     * - Store to store embeddings
     * - Memory system to maintain conversation context
     */
    public DeepSeekR1Context() {
        String ollamaURL = System.getenv("OLLAMA_URL");
        if (ollamaURL == null || ollamaURL.isEmpty() || ollamaURL.isBlank()) {
            throw new IllegalArgumentException("OLLAMA_URL not configured");
        }
        // Initialization of the model for the chat assistant
        this.chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaURL)
                .timeout(Duration.ofSeconds(360))
                .logRequests(true)
                .modelName("deepseek-r1:1.5b")
                .temperature(0.7)
                .build();

        // Initialization of the embedding system with quantized model for optimal performance
        this.embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        // Configuration of chat memory with a window of 10 messages
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Creation of components for intelligent context management
        ContentRetriever contentRetriever = createContentRetriever();
        ContentInjector contentInjector = createContentInjector();

        // Configuration of the RAG system with query compression
        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();
    }

    /**
     * Configures the context injection system that:
     * - Adds relevant metadata to the context
     * - Tracks the source and relevance of information
     */
    private ContentInjector createContentInjector() {
        return DefaultContentInjector.builder()
                .metadataKeysToInclude(java.util.Arrays.asList("source", "relevance"))
                .build();
    }

    /**
     * Creates the context retrieval system that:
     * - Uses semantic embeddings to find relevant content
     * - Sets similarity thresholds to ensure quality results
     * - Limits the number of results to optimize performance
     */
    private ContentRetriever createContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)  // Limits to 5 most relevant results
                .minScore(0.5) // Minimum similarity threshold of 50%
                .build();
    }

    /**
     * Manages the ingestion of new documents into the system:
     * - Splits documents into manageable segments
     * - Generates embeddings for each segment
     * - Stores embeddings for future retrieval
     */
    public void ingestDocument(Document document) {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(550, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
    }

    /**
     * Creates an instance of the assistant that:
     * - Uses the configured chat model
     * - Applies the RAG system for context retrieval
     * - Maintains conversation memory
     */
    public Assistant createAssistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();
    }
}