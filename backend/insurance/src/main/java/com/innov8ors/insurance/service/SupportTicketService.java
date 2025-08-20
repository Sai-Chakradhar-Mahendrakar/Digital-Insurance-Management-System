package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import java.util.List;

public interface SupportTicketService {
    SupportTicket createTicket(SupportTicketCreateRequest request, Long userId);
    List<SupportTicket> getTicketsByUser(Long userId);
    SupportTicket updateTicketStatus(Long ticketId, SupportTicketUpdateRequest request);
    List<SupportTicket> fetchAllTickets();
}
