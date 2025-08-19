package com.innov8ors.insurance.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketCreateRequest {
    private Long userId;
    private Long policyId;
    private Long claimId;
    private String subject;
    private String description;
}
