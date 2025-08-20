package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.repository.ClaimRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimDao extends ClaimRepository {
    default Page<Claim> getByUserId(Long userId, Pageable pageable) {
        return findByUserId(userId, pageable);
    }


    default Optional<Claim> getByClaimIdAndUserId(Long claimId, Long userId) {
        return findByIdAndUserPolicyUserId(claimId, userId);
    }
}
