package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.UserPolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserPolicy> purchasePolicy(Authentication authentication,
                                                     @Valid @RequestBody PolicyPurchaseRequest request) {
        log.debug("Received request to purchase policy for user: {}", authentication.getName());
        String userEmail = authentication.getName();
        UserPolicy userPolicy = userPolicyService.purchasePolicy(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPolicy);
    }

    @GetMapping("/policies")
    public ResponseEntity<Page<UserPolicyResponse>> getUserPolicies(Authentication authentication,
                                                                    @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Fetching policies for user: {}", authentication.getName());
        String userEmail = authentication.getName();
        Page<UserPolicyResponse> userPolicies = userPolicyService.getUserPolicies(userEmail, page, size);
        return ResponseEntity.ok(userPolicies);
    }

}