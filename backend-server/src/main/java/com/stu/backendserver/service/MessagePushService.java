package com.stu.backendserver.service;

import com.stu.backendserver.dto.MessagePayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePushService {

    private final SimpMessagingTemplate messagingTemplate;

    public MessagePushService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToTopic(String destination, MessagePayload payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void sendToUser(String userId, String destination, MessagePayload payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }
}
