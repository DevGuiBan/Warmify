package com.Guilherme.Warmify.utils;

import lombok.Getter;

@Getter
public enum RecoveryKeyStatus {

	AVAILABLE("available"),
	USED("used"),
	BLOCKED("blocked");

	private final String status;

	RecoveryKeyStatus(String status) {
		this.status = status;
	}
}

