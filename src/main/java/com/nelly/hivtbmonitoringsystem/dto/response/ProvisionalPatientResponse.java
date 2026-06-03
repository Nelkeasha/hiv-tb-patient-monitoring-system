package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class ProvisionalPatientResponse {
    private UUID patientId;
    private String patientCode;
    private String fullName;
    private String referralId;
    private String status;
    private String message;
    private String referralInstructions;
}
