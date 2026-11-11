package com.qiutian.capstone.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Agent 配置中心。
 * 只有在环境变量 OPENAI_API_KEY 存在时才创建真实 ChatClient；
 * 否则返回 Optional.empty()，业务层自动降级为离线桩逻辑。
 */
@Configuration
public class AgentConfig {

    @Bean
    public Optional<ChatClient> chatClient() {
        if (KeyGuard.hasKey("OPENAI_API_KEY")) {
            OpenAiApi api = OpenAiApi.builder()
                    .apiKey(KeyGuard.get("OPENAI_API_KEY"))
                    .build();
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .openAiApi(api)
                    .build();
            return Optional.of(ChatClient.create(model));
        }
        return Optional.empty();
    }
}
