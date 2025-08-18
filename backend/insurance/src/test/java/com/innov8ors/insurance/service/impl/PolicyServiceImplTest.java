package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.repository.dao.PolicyDao;
import com.innov8ors.insurance.service.PolicyService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_COVERAGE_AMOUNT;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_DESCRIPTION;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_DURATION_MONTHS;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_NAME;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_PREMIUM_AMOUNT;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_TYPE;
import static com.innov8ors.insurance.util.TestUtil.getPoliciesPage;
import static com.innov8ors.insurance.util.TestUtil.getPolicy;
import static com.innov8ors.insurance.util.TestUtil.getPolicyCreateRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

public class PolicyServiceImplTest {
    PolicyService policyService;

    @Mock
    PolicyDao policyDao;

    AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
        policyService = new PolicyServiceImpl(policyDao);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSuccessfulGetPolicies() {
        doReturn(getPoliciesPage())
                .when(policyDao)
                .getAll(any(), any());

        Page<Policy> policies = policyService.getPolicies(null, 0, 10);

        assertEquals(10, policies.getTotalElements());
        verify(policyDao).getAll(any(), any());
        verifyNoMoreInteractions(policyDao);
    }

    @Test
    public void testSuccessfulAddPolicy() {
        doReturn(getPolicy())
                .when(policyDao)
                .persist(any());

        Policy policy = policyService.addPolicy(getPolicyCreateRequest());

        assertEquals(TEST_POLICY_ID, policy.getId());
        assertEquals(TEST_POLICY_TYPE, policy.getType());
        assertEquals(TEST_POLICY_NAME, policy.getName());
        assertEquals(TEST_POLICY_DESCRIPTION, policy.getDescription());
        assertEquals(TEST_POLICY_PREMIUM_AMOUNT, policy.getPremiumAmount());
        assertEquals(TEST_POLICY_COVERAGE_AMOUNT, policy.getCoverageAmount());
        assertEquals(TEST_POLICY_DURATION_MONTHS, policy.getDurationMonths());
        assertEquals(TEST_POLICY_PREMIUM_AMOUNT, policy.getRenewalPremiumRate());
        verify(policyDao).persist(any(Policy.class));
        verifyNoMoreInteractions(policyDao);
    }

    @Test
    public void testFailureAddPolicy() {
        doThrow(new RuntimeException("error"))
                .when(policyDao)
                .persist(any());

        try {
            policyService.addPolicy(getPolicyCreateRequest());
            fail("Expected an exception to be thrown");
        } catch (Exception ignored) {
            assertEquals("error", ignored.getMessage());
        }
    }
}
