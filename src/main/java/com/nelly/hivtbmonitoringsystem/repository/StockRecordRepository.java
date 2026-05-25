package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, UUID> {
    List<StockRecord> findByChwId(UUID chwId);
    Optional<StockRecord> findByChwIdAndMedicationName(UUID chwId, String medicationName);
    List<StockRecord> findByResupplyRequestedTrue();
    @Query("SELECT s FROM StockRecord s WHERE s.chw.id = :chwId AND s.currentQuantity <= s.reorderLevel")
    List<StockRecord> findByChwIdAndCurrentQuantityBelowReorderLevel(@Param("chwId") UUID chwId);
}
