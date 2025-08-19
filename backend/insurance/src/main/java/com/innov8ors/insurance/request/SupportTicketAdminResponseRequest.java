package com.innov8ors.insurance.request;

import lombok.Data;

@Data
public class SupportTicketAdminResponseRequest {
    private String response;
    private String status; // Should be RESOLVED or CLOSED
}

