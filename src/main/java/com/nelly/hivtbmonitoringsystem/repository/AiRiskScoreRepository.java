package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.AiRiskScore;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiRiskScoreRepository extends JpaRepository<AiRiskScore, UUID> {

    Optional<AiRiskScore> findTopByPatientIdOrderByCalculatedAtDesc(UUID patientId);
    List<AiRiskScore> findByPatientIdOrderByCalculatedAtDesc(UUID patientId);
    List<AiRiskScore> findByRiskLevel(RiskLevel riskLevel);
    List<AiRiskScore> findByRiskLevelIn(List<RiskLevel> riskLevels);

    /** Latest score per patient for all patients assigned to a CHW — for priority list. */
    @Query(value = """
            SELECT DISTINCT ON (ars.patient_id) ars.*
            FROM ai_risk_scores ars
            JOIN patients p ON ars.patient_id = p.id
            WHERE p.chw_id = :chwId
            ORDER BY ars.patient_id, ars.calculated_at DESC
            """, nativeQuery = true)
    List<AiRiskScore> findLatestScoresForChwPatients(@Param("chwId") UUID chwId);

    /** Latest score per patient where risk level is HIGH or CRITICAL — for facility dashboard. */
    @Query(value = """
            SELECT DISTINCT ON (ars.patient_id) ars.*
            FROM ai_risk_scores ars
            WHERE ars.risk_level IN ('HIGH', 'CRITICAL')
            ORDER BY ars.patient_id, ars.calculated_at DESC
            """, nativeQuery = true)
    List<AiRiskScore> findLatestHighRiskScores();

    /** Latest score per patient for all patients at a given facility. */
    @Query(value = """
            SELECT DISTINCT ON (ars.patient_id) ars.*
            FROM ai_risk_scores ars
            JOIN patients p ON ars.patient_id = p.id
            WHERE p.facility_id = :facilityId
            ORDER BY ars.patient_id, ars.calculated_at DESC
            """, nativeQuery = true)
    List<AiRiskScore> findLatestScoresForFacilityPatients(@Param("facilityId") UUID facilityId);
}
