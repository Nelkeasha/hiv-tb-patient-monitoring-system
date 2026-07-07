package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Route B negative resolution — the clinic lab result for a PROVISIONAL screening
 * voucher came back NEGATIVE. Flags the voucher RESOLVED_NEGATIVE and blocks the
 * profile from ever entering the active patient tables (RBC 2022 registry block),
 * then redirects the case to prevention (TB differential-diagnosis / HIV PrEP).
 */
@Data
public class ResolveNegativeRequest {

    /** Optional external lab reference for the negative result (GeneXpert / HIV test). */
    @Size(max = 200, message = "Lab reference must be at most 200 characters")
    private String labReference;

    /** Optional free-text clinical note attached to the resolution. */
    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;
}
