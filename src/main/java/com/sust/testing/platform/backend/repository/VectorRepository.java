package com.sust.testing.platform.backend.repository;

import com.sust.testing.platform.backend.entity.VectorPsychotype;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VectorRepository extends JpaRepository<VectorPsychotype, Long> {
}
