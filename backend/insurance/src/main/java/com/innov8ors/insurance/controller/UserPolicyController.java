package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.UserPolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = "*")
public class UserPolicyController {

    private final UserPolicyService userPolicyService;

    public UserPolicyController(UserPolicyService userPolicyService) {
        this.userPolicyService = userPolicyService;
    }

    @PostMapping("/policy/purchase")
    public ResponseEntity<UserPolicyResponse> purchasePolicy(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Valid @RequestBody PolicyPurchaseRequest request) {
        log.debug("Received request to purchase policy for user: {}", userPrincipal.getUserEmail());
        String userEmail = userPrincipal.getUserEmail();
        UserPolicyResponse userPolicy = userPolicyService.purchasePolicy(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPolicy);
    }

    @GetMapping("/policies")
    public ResponseEntity<UserPolicyPaginatedResponse> getUserPolicies(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                       @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Fetching policies for user: {}", userPrincipal.getUserEmail());
        String userEmail = userPrincipal.getUserEmail();
        UserPolicyPaginatedResponse userPolicies = userPolicyService.getUserPolicies(userEmail, page, size);
        return ResponseEntity.ok(userPolicies);
    }

    @GetMapping("/policy/renewable")
    public ResponseEntity<UserPolicyPaginatedResponse> getRenewablePolicies(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                            @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Fetching renewable policies for user: {}", userPrincipal.getUserEmail());
        Long userId = userPrincipal.getId();
        UserPolicyPaginatedResponse renewablePolicies = userPolicyService.getRenewablePolicies(userId, page, size);
        return ResponseEntity.ok(renewablePolicies);
    }

    @PostMapping("/policy/{policyId}/renew")
    public ResponseEntity<UserPolicyResponse> renewPolicy(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @PathVariable Long policyId) {
        log.debug("Received request to renew policy ID: {}", policyId);
        Long userId = userPrincipal.getId();
        UserPolicyResponse renewedPolicy = userPolicyService.renewPolicy(userId, policyId);
        return ResponseEntity.ok(renewedPolicy);
    }
}