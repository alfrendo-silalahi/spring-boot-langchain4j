package dev.alfrendosilalahi.springbootlangchain.easy;

import dev.alfrendosilalahi.springbootlangchain.easy.util.EasyRAGAssistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static dev.alfrendosilalahi.springbootlangchain.easy.util.Utils.glob;
import static dev.alfrendosilalahi.springbootlangchain.easy.util.Utils.toPath;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Configuration
public class EasyRAGConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public EasyRAGAssistant assistant() {
        List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));
        return AiServices.builder(EasyRAGAssistant.class)
                .chatLanguageModel(OpenAiChatModel.withApiKey(openaiApiKey))
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(createContentRetriever(documents))
                .build();
    }

    private ContentRetriever createContentRetriever(List<Document> documents) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

}
