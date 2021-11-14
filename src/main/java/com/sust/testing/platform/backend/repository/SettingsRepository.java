package com.sust.testing.platform.backend.repository;

import com.sust.testing.platform.backend.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
