package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.innov8ors.insurance.service.SupportTicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Override
    public SupportTicket createTicket(SupportTicketCreateRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(request.getUserId());
        ticket.setPolicyId(request.getPolicyId());
        ticket.setClaimId(request.getClaimId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(SupportTicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        return supportTicketRepository.save(ticket);
    }

    @Override
    public List<SupportTicket> getTicketsByUser(Long userId) {
        return supportTicketRepository.findByUserId(userId);
    }

    @Override
    public SupportTicket updateTicketStatus(Long ticketId, String response, SupportTicketStatus status) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setResponse(response);
            ticket.setStatus(status);
            if (status == SupportTicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            return supportTicketRepository.save(ticket);
        }
        throw new RuntimeException("Support ticket not found");
    }
}
