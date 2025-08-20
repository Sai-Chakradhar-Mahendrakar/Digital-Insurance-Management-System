package com.innov8ors.insurance.response;

import com.innov8ors.insurance.entity.Claim;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimPaginatedResponse {
    private List<Claim> claims;

    private Long totalElements;

    private Integer totalPages;

    private Integer size;

    private Integer page;
}
