package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.DispensingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DispensingEventRepository extends JpaRepository<DispensingEvent, UUID> {
    List<DispensingEvent> findByPatientId(UUID patientId);
    List<DispensingEvent> findByChwId(UUID chwId);
    List<DispensingEvent> findByStockId(UUID stockId);
}
