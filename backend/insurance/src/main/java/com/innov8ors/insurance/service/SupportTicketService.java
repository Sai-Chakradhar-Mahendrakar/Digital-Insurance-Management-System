package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import java.util.List;

public interface SupportTicketService {
    SupportTicket createTicket(SupportTicketCreateRequest request);
    List<SupportTicket> getTicketsByUser(Long userId);
    SupportTicket updateTicketStatus(Long ticketId, String response, SupportTicketStatus status);
}

