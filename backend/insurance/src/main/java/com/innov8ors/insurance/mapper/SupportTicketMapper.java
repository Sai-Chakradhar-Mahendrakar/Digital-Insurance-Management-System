package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;

import java.time.LocalDateTime;

public class SupportTicketMapper {
    public static SupportTicket fromCreateRequest(SupportTicketCreateRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(request.getUserId());
        ticket.setPolicyId(request.getPolicyId());
        ticket.setClaimId(request.getClaimId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(SupportTicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticket;
    }
}

