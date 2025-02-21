package com.example.chatbot.service;

import com.example.chatbot.model.ChatMessage;
import com.example.chatbot.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final List<ChatMessage> conversationHistory = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatMessage> processUserMessage(String userMessage) {
        ChatMessage userChatMessage = saveMessage("User", userMessage);
        conversationHistory.add(userChatMessage);

        String aiResponse = getOllamaResponse(userMessage);

        ChatMessage aiChatMessage = saveMessage("AI", aiResponse);
        conversationHistory.add(aiChatMessage);

        return List.of(userChatMessage, aiChatMessage);
    }

    private String getOllamaResponse(String userMessage) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("model", "mistral");
            requestJson.put("prompt", "Réponds uniquement en français. " + userMessage);
            requestJson.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> requestEntity = new HttpEntity<>(requestJson.toString(), headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(OLLAMA_URL, HttpMethod.POST, requestEntity, String.class);

            JSONObject responseJson = new JSONObject(responseEntity.getBody());
            return responseJson.getString("response");
        } catch (Exception e) {
            return "Désolé, je n'ai pas pu répondre.";
        }
    }

    public ChatMessage saveMessage(String sender, String message) {
        String truncatedMessage = message.length() > 1000 ? message.substring(0, 1000) : message;
        ChatMessage chatMessage = new ChatMessage(sender, truncatedMessage);
        return chatRepository.save(chatMessage);
    }

    public List<ChatMessage> getAllMessages() {
        return chatRepository.findAll();
    }
}
