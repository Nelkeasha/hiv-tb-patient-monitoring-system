package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.MedicationFormulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicationFormularyRepository extends JpaRepository<MedicationFormulary, UUID> {
    List<MedicationFormulary> findByIsActiveTrue();
}
