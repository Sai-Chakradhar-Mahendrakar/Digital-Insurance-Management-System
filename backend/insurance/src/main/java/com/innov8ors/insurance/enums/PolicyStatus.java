package com.innov8ors.insurance.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PolicyStatus {
    ACTIVE,
    EXPIRED,
    RENEWED,
    CANCELLED
}