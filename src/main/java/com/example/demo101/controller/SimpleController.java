package com.example.demo101.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @GetMapping("/test")
    public String getResponse(@RequestParam("q") String query)
    {
        var ollamaApi = new OllamaApi();

        var embedding = OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(OllamaModel.LLAMA3_2_1B)
                                .build())
                .build();

        var store = SimpleVectorStore.builder(embedding).build();

        var reader = new TextReader(new ClassPathResource("/sample.txt"));

        reader.getCustomMetadata().put("filename", "sample.txt");

        var documents = new TokenTextSplitter().split(reader.get());

        store.add(documents);

        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(OllamaModel.LLAMA3_2_1B)
                                .build())
                .build();

        var chat = ChatClient.builder(chatModel).build();

        return chat.prompt(query)
                .advisors(new QuestionAnswerAdvisor(store, new SearchRequest()))
                .call()
                .content();
    }

}
