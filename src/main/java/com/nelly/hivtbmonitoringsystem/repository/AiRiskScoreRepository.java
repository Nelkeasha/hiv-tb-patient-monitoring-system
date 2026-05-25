package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.AiRiskScore;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiRiskScoreRepository extends JpaRepository<AiRiskScore, UUID> {
    Optional<AiRiskScore> findTopByPatientIdOrderByCalculatedAtDesc(UUID patientId);
    List<AiRiskScore> findByPatientId(UUID patientId);
    List<AiRiskScore> findByRiskLevel(RiskLevel riskLevel);
    List<AiRiskScore> findByRiskLevelIn(List<RiskLevel> riskLevels);
}
