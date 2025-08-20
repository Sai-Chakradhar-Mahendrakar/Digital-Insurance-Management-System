package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.repository.UserPolicyRepository;

import java.util.Optional;

public interface UserPolicyDao extends UserPolicyRepository {
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);
}