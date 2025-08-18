package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ClaimStatus status;

    private String reviewerComment;
}
