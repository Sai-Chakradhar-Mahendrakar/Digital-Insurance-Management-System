package com.innov8ors.insurance.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserPolicyStatus {
    ACTIVE,
    EXPIRED,
    RENEWED,
    CANCELLED
}