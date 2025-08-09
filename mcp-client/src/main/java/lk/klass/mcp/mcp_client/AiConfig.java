package lk.klass.mcp.mcp_client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author danushka
 * 2025-08-09
 */
@Configuration
public class AiConfig {
    @Bean
    ChatMemory chatMemoryStore() {
        // swap to RedisChatMemoryStore for multi-instance deployments
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel, ChatMemory store) {
        return ChatClient.builder(chatModel)
                // enable memory by default; grouped by AiHeaders.CONVERSATION_ID
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(store).build())
                .build();
    }
}
