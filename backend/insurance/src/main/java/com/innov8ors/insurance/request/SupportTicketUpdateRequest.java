package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.SupportTicketStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupportTicketUpdateRequest {
    private String response;
    private SupportTicketStatus status;
}
