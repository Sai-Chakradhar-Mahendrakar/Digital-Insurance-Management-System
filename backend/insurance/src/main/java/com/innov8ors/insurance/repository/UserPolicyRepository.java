package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.UserPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPolicyRepository extends BaseRepository<UserPolicy, Long> {

    @Query("SELECT up FROM UserPolicy up JOIN FETCH up.policy WHERE up.userId = :userId")
    Page<UserPolicy> findByUserIdWithPolicy(@Param("userId") Long userId, Pageable pageable);

    List<UserPolicy> findAll(Specification<UserPolicy> specification);

    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);

    @Query("SELECT up FROM UserPolicy up JOIN FETCH up.policy WHERE up.userId = :userId AND ((up.endDate BETWEEN :startDate AND :endDate AND up.status = 'ACTIVE') OR up.status = 'EXPIRED')")
    Page<UserPolicy> findActiveNearingExpiryOrExpiredPolicies(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}