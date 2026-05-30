package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FacilityResponse {
    private UUID id;
    private String name;
    private String location;
    private String district;
    private Boolean isActive;
}
