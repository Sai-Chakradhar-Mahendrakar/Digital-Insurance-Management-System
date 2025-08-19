package com.innov8ors.insurance.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketCreateRequest {
    @NotNull
    private Long userId;
    private Long policyId;
    private Long claimId;
    @NotNull
    @Size(min = 10)
    private String subject;
    @NotNull
    @Size(min = 15)
    private String description;
}
