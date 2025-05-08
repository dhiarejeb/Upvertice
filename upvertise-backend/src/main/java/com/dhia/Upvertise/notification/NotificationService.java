package com.dhia.Upvertise.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId , Notification notification) {
        log.info("Sending notification to user {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
                userId,
                "/notifications",
                notification
        );
    }
//add a method to send to a topic:
    public void sendNotificationToRole(String role, Notification notification) {
        String destination = "/topic/" + role;
        log.info("Sending notification to role {} at {} with payload {}", role, destination, notification);
        messagingTemplate.convertAndSend(destination, notification);
    }
}
