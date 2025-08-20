package com.innov8ors.insurance.request;

import lombok.Data;
import com.innov8ors.insurance.enums.SupportTicketStatus;

@Data
public class SupportTicketUpdateRequest {
    private String response;
    private SupportTicketStatus status;
}
