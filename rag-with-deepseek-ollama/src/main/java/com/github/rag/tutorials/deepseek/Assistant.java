package com.github.rag.tutorials.deepseek;

import dev.langchain4j.service.SystemMessage;

/**
 * Interface for the assistant that manages the conversation
 */
public interface Assistant {
    @SystemMessage({
            "You are a helpful assistant.",
            "When unsure, say 'I don't know'.",
            "Use only the provided context for answers."
    })
    String chat(String message);
}
