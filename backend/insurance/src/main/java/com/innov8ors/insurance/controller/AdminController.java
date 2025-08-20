package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private final PolicyService policyService;

    private final UserPolicyService userPolicyService;

    public AdminController(PolicyService policyService, UserPolicyService userPolicyService) {
        this.policyService = policyService;
        this.userPolicyService = userPolicyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public Policy addPolicy(@Valid @RequestBody PolicyCreateRequest policyCreateRequest) {
        log.debug("Received request to add policy: {}", policyCreateRequest);
        return policyService.addPolicy(policyCreateRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/policies/{policyId}/users")
    public Page<UserPolicyResponse> getUsersByPolicyId(
            @PathVariable Long policyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Fetching users for policy ID: {}", policyId);
        return userPolicyService.getUsersByPolicyId(policyId, page, size);
    }

}
