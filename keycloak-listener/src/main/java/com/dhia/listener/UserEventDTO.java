package com.dhia.listener;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEventDTO {
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String eventType;
    private long timestamp;
}
