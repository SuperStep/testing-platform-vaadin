package com.sust.testing.platform.ui.view.login;

public class AccessDeniedException extends RuntimeException {
	public AccessDeniedException() {
	}

	public AccessDeniedException(String message) {
		super(message);
	}
}
