package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.dao.PolicyDao;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.service.PolicyService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.innov8ors.insurance.mapper.PolicyMapper.getPolicyFromRequest;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.POLICY_NOT_FOUND;
import static com.innov8ors.insurance.util.Constant.PolicyConstants.POLICY_NAME_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.PolicyConstants.POLICY_TYPE_PLACEHOLDER;

@Service
@Slf4j
public class PolicyServiceImpl implements PolicyService {
    private final PolicyDao policyDao;

    public PolicyServiceImpl(PolicyDao policyDao) {
        this.policyDao = policyDao;
    }

    @Override
    public Page<Policy> getPolicies(String type, Integer page, Integer size) {
        Specification<Policy> specification = getPoliciesQuery(type);
        log.debug("Fetching policies with type: {}, page: {}, size: {}", type, page, size);
        return policyDao.getAll(specification, PageRequest.of(page, size, Sort.by(POLICY_NAME_PLACEHOLDER).ascending()));
    }

    @Override
    public Policy addPolicy(PolicyCreateRequest policyCreateRequest) {
        Policy policy = getPolicyFromRequest(policyCreateRequest);
        log.debug("Adding new policy: {}", policy);
        return policyDao.persist(policy);
    }

    @Override
    public Policy getById(Long policyId) {
        log.debug("Checking if policy exists with ID: {}", policyId);
        Optional<Policy> policyOptional = policyDao.findById(policyId);
        if(policyOptional.isEmpty()) {
            log.error("Policy with ID {} not found", policyId);
            throw new NotFoundException(POLICY_NOT_FOUND);
        }
        log.info("Policy found: {}", policyOptional.get());
        return policyOptional.get();
    }

    private Specification<Policy> getPoliciesQuery(String type) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (StringUtils.isNotBlank(type)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(POLICY_TYPE_PLACEHOLDER), type));
            }
            return predicate;
        };
    }
}
