package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StaffResponse {

    private UUID userId;
    private UUID staffId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private String facilityName;
    private String temporaryPassword;
    private LocalDateTime createdAt;
}
