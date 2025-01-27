package com.github.rag.tutorials.smart.context;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Implementation of Smart Context Management that optimizes the use of context in interactions with LLM
 * using advanced context management techniques such as:
 * - Query compression
 * - Re-ranking of results
 * - Intelligent conversation memory management
 * - Semantic embeddings for retrieving relevant information
 */
public class SmartContextManager {

    private final ChatLanguageModel chatModel;
    private final ScoringModel scoringModel;
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
    public SmartContextManager() {
        String claudeApiKey = System.getenv("CLAUDE_API_KEY");
        if (claudeApiKey == null || claudeApiKey.isEmpty() || claudeApiKey.isBlank()) {
            throw new IllegalArgumentException("CLAUDE_API_KEY not configured");
        }
        String cohereApiKey = System.getenv("COHERE_API_KEY");
        if (cohereApiKey == null || cohereApiKey.isEmpty() || cohereApiKey.isBlank()) {
            throw new IllegalArgumentException("COHERE_API_KEY not configured");
        }

        // Initialization of the model for context retrieval
        ChatLanguageModel contextModel = AnthropicChatModel.builder()
                .modelName(AnthropicChatModelName.CLAUDE_3_5_HAIKU_20241022)
                .logRequests(true)
                .apiKey(claudeApiKey)
                .build();
        // Initialization of the model for the chat assistant
        this.chatModel = AnthropicChatModel.builder()
                .logRequests(true)
                .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022)
                .apiKey(claudeApiKey)
                .build();

        this.scoringModel = CohereScoringModel.builder()
                .apiKey(cohereApiKey)
                .logRequests(true)
                .modelName("rerank-multilingual-v3.0")
                .build();

        // Initialization of the embedding system with quantized model for optimal performance
        this.embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        // Configuration of chat memory with a window of 10 messages
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Creation of components for intelligent context management
        ContentRetriever contentRetriever = createContentRetriever();
        ContentAggregator contentAggregator = createContentAggregator();
        ContentInjector contentInjector = createContentInjector();

        // Configuration of the RAG system with query compression
        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(new CompressingQueryTransformer(contextModel))
                .contentRetriever(contentRetriever)
                .contentAggregator(contentAggregator)
                .contentInjector(contentInjector)
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
     * Configures the re-ranking system that:
     * - Reorders results based on relevance
     * - Applies a more precise second phase of filtering
     * - Uses a model to evaluate relevance
     */
    private ContentAggregator createContentAggregator() {
        return ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                .minScore(0.6) // Higher threshold for re-ranking
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