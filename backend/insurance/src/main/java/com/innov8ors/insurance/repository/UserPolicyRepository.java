package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPolicyRepository extends BaseRepository<UserPolicy, Long> {

    @Query("SELECT up FROM UserPolicy up JOIN FETCH up.policy WHERE up.userId = :userId")
    Page<UserPolicy> findByUserIdWithPolicy(@Param("userId") Long userId, Pageable pageable);

    List<UserPolicy> findByStatusAndEndDateBefore(UserPolicyStatus status, LocalDateTime date);

    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);
}