package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Referral;
import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    List<Referral> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    List<Referral> findByReferredByChwIdOrderByCreatedAtDesc(UUID chwId);
    List<Referral> findByStatusOrderByCreatedAtDesc(ReferralStatus status);
    List<Referral> findByPatientFacilityIdOrderByCreatedAtDesc(UUID facilityId);
    List<Referral> findByPatientFacilityIdAndStatusOrderByCreatedAtDesc(UUID facilityId, ReferralStatus status);

    /**
     * Referrals with a confirmed appointment date that has now passed but the
     * patient never attended (status still CONFIRMED or MODIFIED).
     * These are the primary trigger for auto-generated tracing tasks.
     */
    @Query("SELECT r FROM Referral r WHERE r.status IN ('CONFIRMED', 'MODIFIED') " +
           "AND r.facilityAppointmentDate IS NOT NULL " +
           "AND r.facilityAppointmentDate < :today")
    List<Referral> findMissedAppointments(@Param("today") LocalDate today);
}
