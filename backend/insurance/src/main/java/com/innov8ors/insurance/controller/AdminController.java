package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.service.PolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private final PolicyService policyService;

    public AdminController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public Policy addPolicy(@Valid @RequestBody PolicyCreateRequest policyCreateRequest) {
        log.debug("Received request to add policy: {}", policyCreateRequest);
        return policyService.addPolicy(policyCreateRequest);
    }
}
