package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class MedicationFormularyResponse {
    private UUID id;
    private String name;
    private String dosageForm;
}
