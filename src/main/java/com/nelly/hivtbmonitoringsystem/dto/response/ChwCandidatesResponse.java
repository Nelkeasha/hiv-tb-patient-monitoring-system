package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * CHW assignment candidates for a patient's village — drives the provider-side
 * assignment UI:
 * <ul>
 *   <li>{@code SINGLE}   — one village CHW; auto-assigned, shown read-only.</li>
 *   <li>{@code MULTIPLE} — several village CHWs; a required dropdown of them.</li>
 *   <li>{@code NONE}     — no village CHW; warning + fallback dropdown listing
 *       all the facility's active CHWs so the patient is never unassigned.</li>
 * </ul>
 */
@Getter
@Builder
public class ChwCandidatesResponse {

    public enum Mode { SINGLE, MULTIPLE, NONE }

    private final Mode mode;
    private final List<Candidate> candidates;

    @Getter
    @Builder
    public static class Candidate {
        private final UUID id;
        private final String fullName;
        private final String assignedVillage;
        private final long activePatients;
    }
}
