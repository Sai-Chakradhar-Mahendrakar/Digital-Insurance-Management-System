package com.innov8ors.insurance.request;


import lombok.Data;
@Data
public class NotificationSendRequest {
    private Long userId; // Optional: if null, send to group or all
    private String title;
    private String message;
    // Optionally, add group or policy/claim targeting fields
}
