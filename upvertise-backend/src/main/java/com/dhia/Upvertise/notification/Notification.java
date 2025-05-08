package com.dhia.Upvertise.notification;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private String message;
    private NotificationStatus status;
}
