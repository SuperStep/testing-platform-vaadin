package com.sust.testing.platform.backend.repository;


import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.entity.VerificationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokensRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
