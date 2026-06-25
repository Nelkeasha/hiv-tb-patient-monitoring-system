package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserId(UUID userId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTargetTableAndTargetId(String targetTable, UUID targetId);

    /** Tail of the hash chain — the most recently written chained entry, to link the next one onto. */
    @Query("SELECT a FROM AuditLog a WHERE a.entryHash IS NOT NULL ORDER BY a.createdAt DESC, a.id DESC")
    List<AuditLog> findChainTail(Pageable pageable);

    /** Every chained entry in write order, for chain-integrity verification. */
    List<AuditLog> findByEntryHashIsNotNullOrderByCreatedAtAscIdAsc();
}
