package com.innov8ors.insurance.entity;

import com.innov8ors.insurance.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "claim", schema = "public")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_policy_id", nullable = false)
    private Long userPolicyId;

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;

    @Column(name = "claim_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal claimAmount;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;

    @Column(name = "reviewer_comment", length = 1000)
    private String reviewerComment;

    @Column(name = "resolved_date")
    private LocalDate resolvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_policy_id", insertable = false, updatable = false)
    private UserPolicy userPolicy;

    public Claim(Long userPolicyId, LocalDate claimDate, BigDecimal claimAmount, String reason) {
        this.userPolicyId = userPolicyId;
        this.claimDate = claimDate;
        this.claimAmount = claimAmount;
        this.reason = reason;
        this.status = ClaimStatus.PENDING;
    }
}