package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Builder
public class ChwPriorityListResponse {
    private UUID chwId;
    private LocalDateTime generatedAt;
    private List<PriorityPatientResponse> visitToday;
    private List<PriorityPatientResponse> callToday;
    private List<PriorityPatientResponse> stable;
    private int totalPatients;
}
