package com.nelly.hivtbmonitoringsystem.dto.report;

import lombok.Builder;
import lombok.Getter;

/**
 * A single machine-generated, ranked action for the report reader. Produced by
 * a deterministic rule set (see the report model service) so every
 * recommendation is explainable and reproducible — no black-box inference in
 * the care pathway.
 */
@Getter
@Builder
public class Recommendation {

    public enum Severity { CRITICAL, WARNING, INFO }

    private final Severity severity;
    /** What the data shows (the trigger). */
    private final String finding;
    /** The concrete action to take. */
    private final String action;
    /** Who should act on it. */
    private final String owner;
}
