package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimStatusUpdateRequest {
    private ClaimStatus status;

    private String reviewerComment;
}
