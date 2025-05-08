package com.dhia;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final JavaMailSender mailSender;
    private final UserClientService userClientService;


    @KafkaListener(topics = "adminNotificationsTopic", groupId = "email-service-group")
    public void listenAdminNotifications(String message) {
        sendRoleEmails(userClientService.getAdminEmails(), "Admin Notification", message);
    }

    @KafkaListener(topics = "advertiserNotificationsTopic", groupId = "email-service-group")
    public void listenAdvertiserNotifications(String message) {
        sendRoleEmails(userClientService.getAdvertiserEmails(), "New Sponsor Offer Available!", message);
    }

    @KafkaListener(topics = "providerNotificationsTopic", groupId = "email-service-group")
    public void listenProviderNotifications(String message) {
        sendRoleEmails(userClientService.getProviderEmails(), "Provider Notification", message);
    }

    @KafkaListener(topics = "supplierNotificationsTopic", groupId = "email-service-group")
    public void listenSupplierNotifications(String message) {
        sendRoleEmails(userClientService.getSupplierEmails(), "Supplier Notification", message);
    }

    private void sendRoleEmails(List<String> emails, String subject, String message) {
        if (emails == null || emails.isEmpty()) {
            log.warn("No emails found for subject: {}", subject);
            return;
        }
        emails.forEach(email -> sendEmail(email, subject, message));
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // Use `true` for HTML emails
            mailSender.send(mimeMessage);
            log.info("Email sent to {}", to);
        } catch (MailException | MessagingException | jakarta.mail.MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
    @KafkaListener(topics = "sponsorOfferTopic", groupId = "email-service-group")
    public void listenSponsorOfferEvents(String message) {
        List<String> advertiserEmails = userClientService.getAdvertiserEmails();

        for (String email : advertiserEmails) {
            sendEmail(email, "New Sponsor Offer Available!", message);
        }
    }

}
