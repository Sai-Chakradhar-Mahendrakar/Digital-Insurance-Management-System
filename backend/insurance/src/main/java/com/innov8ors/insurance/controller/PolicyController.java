package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.response.PolicyPaginatedResponse;
import com.innov8ors.insurance.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.innov8ors.insurance.mapper.PolicyMapper.getResponseFromPoliciesPage;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping("/policies")
    public PolicyPaginatedResponse getPolicy(@RequestParam(name = "type", required = false) Optional<String> type,
                                             @RequestParam(name = "page", defaultValue = "0") Integer page,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Received request to get policies with type: {}, page: {}, size: {}", type.orElse("all"), page, size);
        Page<Policy> policiesPage = policyService.getPolicies(type.orElse(null), page, size);
        return getResponseFromPoliciesPage(policiesPage, page, size);
    }
}
