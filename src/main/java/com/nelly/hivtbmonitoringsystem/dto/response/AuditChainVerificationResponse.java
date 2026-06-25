package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuditChainVerificationResponse {
    private boolean intact;
    private int entriesChecked;
    private UUID brokenAtEntryId;
    private String reason;

    public static AuditChainVerificationResponse intact(int entriesChecked) {
        return AuditChainVerificationResponse.builder()
                .intact(true)
                .entriesChecked(entriesChecked)
                .build();
    }

    public static AuditChainVerificationResponse broken(UUID entryId, String reason, int entriesChecked) {
        return AuditChainVerificationResponse.builder()
                .intact(false)
                .entriesChecked(entriesChecked)
                .brokenAtEntryId(entryId)
                .reason(reason)
                .build();
    }
}
