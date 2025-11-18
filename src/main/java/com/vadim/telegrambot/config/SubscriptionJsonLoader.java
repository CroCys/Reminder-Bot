package com.vadim.telegrambot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.vadim.telegrambot.dto.SubscriptionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class SubscriptionJsonLoader {

    @Value("${subscription.jsonFilePath}")
    private String filePath;

    private final JsonMapper mapper = new JsonMapper();

    public List<SubscriptionTemplate> loadSubscriptions() {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("Subscription JSON file not found at path: " + filePath);
        }

        try {
            return mapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse subscription JSON file" + filePath + e);
        }
    }
}
