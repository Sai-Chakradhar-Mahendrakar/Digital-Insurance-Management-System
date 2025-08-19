package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketAdminResponseRequest;
import java.util.List;

public interface SupportTicketService {
    SupportTicket createTicket(SupportTicketCreateRequest request);
    List<SupportTicket> getTicketsByUserId(Long userId);
    SupportTicket respondToTicket(Long ticketId, SupportTicketAdminResponseRequest request);
}

