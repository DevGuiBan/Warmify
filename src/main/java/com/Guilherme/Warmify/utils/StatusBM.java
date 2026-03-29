package com.Guilherme.Warmify.utils;

import lombok.Getter;

@Getter
public enum StatusBM {

    VERIFIED("verified"),
    NOTVERIFIED("notverified"),
    ANALYZE("analyze"),
    ASSIGNED("assigned"),
    RESTRICTED("restricted"),
    BLOCKED("blocked");

    private final String status;

    StatusBM(String status) {
        this.status = status;
    }
}