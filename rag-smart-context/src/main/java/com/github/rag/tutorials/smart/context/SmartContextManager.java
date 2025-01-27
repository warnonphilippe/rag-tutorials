package com.github.rag.tutorials.smart.context;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
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
 * Implementazione di Smart Context Management che ottimizza l'utilizzo del contesto nelle interazioni con LLM
 * utilizzando tecniche avanzate di gestione del contesto come:
 * - Compressione delle query
 * - Re-ranking dei risultati
 * - Gestione intelligente della memoria delle conversazioni
 * - Embedding semantici per il recupero di informazioni rilevanti 
 */
public class SmartContextManager {

    private final ChatLanguageModel chatModel;
    private final ScoringModel scoringModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final RetrievalAugmentor retrievalAugmentor;
    private final MessageWindowChatMemory chatMemory;

    /**
     * Inizializza il sistema di gestione del contesto configurando:
     * - Modello di chat per l'interazione con l'LLM
     * - Modello di embedding per la codifica semantica dei testi
     * - Store per memorizzare gli embedding
     * - Sistema di memoria per mantenere il contesto della conversazione
     */
    public SmartContextManager() {
        String openAiApiKey = System.getenv("OPENAI_API_KEY");
        if (openAiApiKey == null || openAiApiKey.isEmpty() || openAiApiKey.isBlank()) {
            throw new IllegalArgumentException("OPENAI_API_KEY non configurata");
        }
        String cohereApiKey = System.getenv("COHERE_API_KEY");
        if (cohereApiKey == null || cohereApiKey.isEmpty() || cohereApiKey.isBlank()) {
            throw new IllegalArgumentException("COHERE_API_KEY non configurata");
        }
        
        // Inizializzazione del modello per il recupero del contesto
        ChatLanguageModel contextModel = OpenAiChatModel.builder()
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                .apiKey(openAiApiKey)
                .build();
        // Inizializzazione del modello per l'assistente di chat
        this.chatModel = OpenAiChatModel.builder()
                .modelName(OpenAiChatModelName.GPT_4_O)
                .apiKey(openAiApiKey)
                .build();

        this.scoringModel = CohereScoringModel.builder()
                .apiKey(cohereApiKey)
                .modelName("rerank-multilingual-v3.0")
                .build();
        // Inizializzazione del sistema di embedding con modello quantizzato per performance ottimali
        this.embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        // Configurazione della memoria di chat con finestra di 10 messaggi
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Creazione dei componenti per la gestione intelligente del contesto
        ContentRetriever contentRetriever = createContentRetriever();
        ContentAggregator contentAggregator = createContentAggregator();
        ContentInjector contentInjector = createContentInjector();

        // Configurazione del sistema RAG con compressione delle query
        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(new CompressingQueryTransformer(contextModel))
                .contentRetriever(contentRetriever)
                .contentAggregator(contentAggregator)
                .contentInjector(contentInjector)
                .build();
    }

    /**
     * Crea il sistema di recupero del contesto che:
     * - Utilizza embedding semantici per trovare contenuti rilevanti
     * - Imposta soglie di similarità per garantire risultati di qualità
     * - Limita il numero di risultati per ottimizzare le performance
     */
    private ContentRetriever createContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)  // Limita a 3 risultati più rilevanti
                .minScore(0.7) // Soglia minima di similarità del 70%
                .build();
    }

    /**
     * Configura il sistema di re-ranking che:
     * - Riordina i risultati in base alla rilevanza
     * - Applica una seconda fase di filtraggio più precisa
     * - Utilizza un modello per valutare la pertinenza
     */
    private ContentAggregator createContentAggregator() {
        return ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                .minScore(0.8) // Soglia più alta per il re-ranking
                .build();
    }

    /**
     * Configura il sistema di iniezione del contesto che:
     * - Aggiunge metadati rilevanti al contesto
     * - Traccia la fonte e la rilevanza delle informazioni
     */
    private ContentInjector createContentInjector() {
        return DefaultContentInjector.builder()
                .metadataKeysToInclude(java.util.Arrays.asList("source", "relevance"))
                .build();
    }

    /**
     * Gestisce l'inserimento di nuovi documenti nel sistema:
     * - Suddivide i documenti in segmenti gestibili
     * - Genera embedding per ogni segmento
     * - Memorizza gli embedding per il recupero futuro
     */
    public void ingestDocument(Document document) {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
    }

    

    /**
     * Crea un'istanza dell'assistente che:
     * - Utilizza il modello di chat configurato
     * - Applica il sistema RAG per il recupero del contesto
     * - Mantiene la memoria della conversazione
     */
    public Assistant createAssistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();
    }
}