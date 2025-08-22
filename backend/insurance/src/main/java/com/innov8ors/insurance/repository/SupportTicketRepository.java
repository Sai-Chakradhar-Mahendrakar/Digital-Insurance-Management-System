package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.SupportTicket;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportTicketRepository extends BaseRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserId(Long userId);
}
