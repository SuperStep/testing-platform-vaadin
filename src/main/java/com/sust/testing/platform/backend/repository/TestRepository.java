package com.sust.testing.platform.backend.repository;

import com.sust.testing.platform.backend.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
