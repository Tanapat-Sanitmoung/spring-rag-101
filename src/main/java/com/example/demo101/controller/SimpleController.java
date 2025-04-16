package com.example.demo101.controller;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @Autowired
    ChatClient.Builder builder;

    @Autowired
    SyncMcpToolCallbackProvider toolProvider;

    @GetMapping("/test")
    public String getResponse()
    {
        var chat = builder.build();

        var query = """
        Please explain about LLM and write it to file /projects/haiku.txt
        """;

        return chat.prompt(query)
                .tools(toolProvider)
                .call()
                .content();
    }

}
