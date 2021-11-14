package com.sust.testing.platform.security;


import com.sust.testing.platform.backend.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
