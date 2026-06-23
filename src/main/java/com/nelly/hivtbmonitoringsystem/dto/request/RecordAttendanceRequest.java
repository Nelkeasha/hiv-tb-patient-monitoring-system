package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecordAttendanceRequest {

    @NotNull(message = "Please record whether the patient attended (ATTENDED or NOT_ATTENDED)")
    private ReferralStatus status; // ATTENDED or NOT_ATTENDED

    @Size(max = 1000, message = "Attendance notes must be at most 1000 characters")
    private String attendanceNotes;
}
