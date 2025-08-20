package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.repository.UserPolicyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserPolicyDao extends UserPolicyRepository {
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);

    @Query("SELECT up FROM UserPolicy up JOIN FETCH up.user WHERE up.policyId = :policyId")
    Page<UserPolicy> findByPolicyIdWithUser(@Param("policyId") Long policyId, Pageable pageable);
}