package com.Guilherme.Warmify.utils;

import lombok.Getter;

@Getter
public enum StatusAccount {

    AVAILABLE("available"),
    MAPPED("mapped"),
    UNAVAILABLE("unavailable");

    private final String status;

    StatusAccount(String status) {
        this.status = status;
    }
}
