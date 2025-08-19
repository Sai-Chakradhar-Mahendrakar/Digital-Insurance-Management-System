package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketAdminResponseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.innov8ors.insurance.service.SupportTicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public SupportTicket createTicket(SupportTicketCreateRequest request) {
        SupportTicket ticket = SupportTicket.builder()
                .userId(request.getUserId())
                .policyId(request.getPolicyId())
                .claimId(request.getClaimId())
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(SupportTicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        return supportTicketRepository.save(ticket);
    }

    @Override
    public List<SupportTicket> getTicketsByUserId(Long userId) {
        return supportTicketRepository.findByUserId(userId);
    }

    @Override
    public SupportTicket respondToTicket(Long ticketId, SupportTicketAdminResponseRequest request) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new RuntimeException("Support ticket not found");
        }
        SupportTicket ticket = optionalTicket.get();
        ticket.setResponse(request.getResponse());
        SupportTicketStatus newStatus = SupportTicketStatus.valueOf(request.getStatus().toUpperCase());
        ticket.setStatus(newStatus);
        if (newStatus == SupportTicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        return supportTicketRepository.save(ticket);
    }
}
