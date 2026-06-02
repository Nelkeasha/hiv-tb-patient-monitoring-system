package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.TracingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TracingTaskRepository extends JpaRepository<TracingTask, UUID> {

    /** All active tasks assigned to a CHW (for daily task list). */
    List<TracingTask> findByChwIdAndStatusNotOrderByMissedAppointmentDateAsc(UUID chwId, String status);

    /** Full tracing history for a patient. */
    List<TracingTask> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    /** All tasks at ESCALATED status (for supervisor view). */
    List<TracingTask> findByStatusOrderByLtfuConfirmedAtDesc(String status);

    /** All active tasks across the system (for scheduler). */
    @Query("SELECT t FROM TracingTask t WHERE t.status NOT IN ('RESOLVED')")
    List<TracingTask> findAllActive();

    /** Check whether an open task already exists for this patient + appointment date. */
    @Query("SELECT t FROM TracingTask t WHERE t.patient.id = :patientId " +
           "AND t.missedAppointmentDate = :date AND t.status <> 'RESOLVED'")
    Optional<TracingTask> findOpenTaskByPatientAndDate(@Param("patientId") UUID patientId,
                                                       @Param("date") LocalDate date);

    /** All active tasks for a CHW ordered by urgency (days elapsed descending). */
    @Query("SELECT t FROM TracingTask t WHERE t.chw.id = :chwId " +
           "AND t.status NOT IN ('RESOLVED') ORDER BY t.daysSinceMissed DESC")
    List<TracingTask> findActiveByCHWOrderByUrgency(@Param("chwId") UUID chwId);

    /** Count of escalated/LTFU confirmed tasks for supervisor dashboard. */
    long countByStatus(String status);
}
