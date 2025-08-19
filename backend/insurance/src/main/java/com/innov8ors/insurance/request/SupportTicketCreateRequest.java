package com.innov8ors.insurance.request;

import lombok.Data;

@Data
public class SupportTicketCreateRequest {
    private Long userId;
    private Long policyId;
    private Long claimId;
    private String subject;
    private String description;
}

