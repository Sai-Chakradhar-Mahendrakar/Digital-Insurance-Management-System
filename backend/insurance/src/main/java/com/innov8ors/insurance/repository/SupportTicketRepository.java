package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.SupportTicket;

import java.util.List;

public interface SupportTicketRepository extends BaseRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserId(Long userId);
}
