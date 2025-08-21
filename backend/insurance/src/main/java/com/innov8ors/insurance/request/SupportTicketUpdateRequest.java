package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.SupportTicketStatus;
import lombok.Data;

@Data
public class SupportTicketUpdateRequest {
    private String response;
    private SupportTicketStatus status;
}
