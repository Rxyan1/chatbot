package com.example.chatbot.controller;

import com.example.chatbot.model.ChatMessage;
import com.example.chatbot.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public List<ChatMessage> sendMessage(@RequestParam String message) {
        return chatService.processUserMessage(message);
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
        return chatService.getAllMessages();
    }
}
