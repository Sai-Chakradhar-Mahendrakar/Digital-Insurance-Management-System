package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final PolicyService policyService;

    public AdminController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public Policy addPolicy(@Valid @RequestBody PolicyCreateRequest policyCreateRequest) {
        return policyService.addPolicy(policyCreateRequest);
    }
}
