package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.Policy;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PolicyRepository extends BaseRepository<Policy, Long> {
}
