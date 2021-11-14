package com.sust.testing.platform.backend.entity;

public class Role {

	// This role implicitly allows access to all views.
	public static final String ADMIN = "admin";
	public static final String USER = "user";

	private Role() {
		// Static methods and fields only
	}

	public static String[] getAllRoles() {
		return new String[] { ADMIN, USER };
	}

}
