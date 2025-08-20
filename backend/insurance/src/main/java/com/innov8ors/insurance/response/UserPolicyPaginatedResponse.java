package com.innov8ors.insurance.response;

import com.innov8ors.insurance.entity.UserPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPolicyPaginatedResponse {
    private List<UserPolicyResponse> userPolicies;

    private Long totalElements;

    private Integer totalPages;

    private Integer size;

    private Integer page;
}
