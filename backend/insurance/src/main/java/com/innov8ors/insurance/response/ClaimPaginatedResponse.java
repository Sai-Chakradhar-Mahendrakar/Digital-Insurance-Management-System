package com.innov8ors.insurance.response;

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
    private List<ClaimResponse> claims;

    private Long totalElements;

    private Integer totalPages;

    private Integer size;

    private Integer page;
}
