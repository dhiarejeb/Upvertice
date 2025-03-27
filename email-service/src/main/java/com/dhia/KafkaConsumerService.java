package com.dhia;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final JavaMailSender mailSender;
    private final UserClientService userClientService;



    @KafkaListener(topics = "sponsorOfferTopic", groupId = "email-service-group")
    public void listenSponsorOfferEvents(String message) {
        List<String> advertiserEmails = userClientService.getAdvertiserEmails();

        for (String email : advertiserEmails) {
            sendEmail(email, "New Sponsor Offer Available!", message);
        }
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
