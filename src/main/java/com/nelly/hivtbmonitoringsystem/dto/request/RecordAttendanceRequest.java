package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordAttendanceRequest {

    @NotNull
    private ReferralStatus status; // ATTENDED or NOT_ATTENDED

    private String attendanceNotes;
}
