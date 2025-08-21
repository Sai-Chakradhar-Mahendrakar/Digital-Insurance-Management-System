package com.innov8ors.insurance.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketCreateRequest {
    private Long policyId;
    private Long claimId;
    @NotNull
    @Size(min = 10)
    @Size(max=80)
    private String subject;
    @NotNull
    private String description;
}
