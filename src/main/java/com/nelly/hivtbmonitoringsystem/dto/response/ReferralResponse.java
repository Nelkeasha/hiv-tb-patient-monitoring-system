package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import com.nelly.hivtbmonitoringsystem.enums.ReferralUrgency;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReferralResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private String patientCode;
    private UUID chwId;
    private String chwName;
    private UUID providerId;
    private String providerName;
    private LocalDate referralDate;
    private String referralReason;
    private ReferralUrgency urgency;
    private ReferralStatus status;
    private LocalDate facilityAppointmentDate;
    private String providerNotes;
    private String attendanceNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
