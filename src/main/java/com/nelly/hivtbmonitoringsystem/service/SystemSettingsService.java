package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.UpdateSystemSettingsRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.SystemSettingsResponse;
import com.nelly.hivtbmonitoringsystem.entity.SystemSettings;
import com.nelly.hivtbmonitoringsystem.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single global row of admin-configurable thresholds (V15 migration seeds it).
 * Read by dose-schedule defaults, the AI risk-scoring service (shared DB read),
 * and the admin settings screen.
 */
@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository repository;

    public SystemSettings getEntity() {
        return repository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "system_settings row missing — V15 migration should have seeded it"));
    }

    public SystemSettingsResponse get() {
        return toResponse(getEntity());
    }

    @Transactional
    public SystemSettingsResponse update(UpdateSystemSettingsRequest req) {
        SystemSettings settings = getEntity();

        if (req.getMissedDoseThreshold() != null)   settings.setMissedDoseThreshold(req.getMissedDoseThreshold());
        if (req.getLowStockDays() != null)           settings.setLowStockDays(req.getLowStockDays());
        if (req.getConfirmWindowMinutes() != null)   settings.setConfirmWindowMinutes(req.getConfirmWindowMinutes());
        if (req.getHighRiskThreshold() != null)      settings.setHighRiskThreshold(req.getHighRiskThreshold());
        if (req.getCriticalRiskThreshold() != null)  settings.setCriticalRiskThreshold(req.getCriticalRiskThreshold());

        return toResponse(repository.save(settings));
    }

    private SystemSettingsResponse toResponse(SystemSettings s) {
        return SystemSettingsResponse.builder()
                .missedDoseThreshold(s.getMissedDoseThreshold())
                .lowStockDays(s.getLowStockDays())
                .confirmWindowMinutes(s.getConfirmWindowMinutes())
                .highRiskThreshold(s.getHighRiskThreshold())
                .criticalRiskThreshold(s.getCriticalRiskThreshold())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
