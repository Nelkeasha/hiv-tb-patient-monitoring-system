package com.nelly.hivtbmonitoringsystem.dto.report;

import lombok.Builder;
import lombok.Getter;

/**
 * One headline number in a management report, with an optional comparison to
 * the previous period and a Red/Amber/Green status. Rendered as a KPI card in
 * the PDF and (later) as a stat tile on the web report.
 *
 * <p>{@code previousValue}/{@code deltaLabel}/{@code trend} are only populated
 * for <em>flow</em> indicators (things that happen within a period — adherence,
 * missed doses, visits, enrolments). <em>Stock</em> indicators (a point-in-time
 * count like "patients currently on treatment") leave them null because the
 * system does not yet store historical snapshots to compare against.
 */
@Getter
@Builder
public class Indicator {

    /** Direction of movement vs the previous period. */
    public enum Trend { UP, DOWN, FLAT, NONE }

    /** Red/Amber/Green health of the indicator against its target. */
    public enum Rag { GOOD, WATCH, BAD, NEUTRAL }

    private final String label;
    private final String value;
    private final String previousValue;  // nullable
    private final String deltaLabel;     // nullable, e.g. "+4.2 pts" or "-8"
    private final Trend trend;
    private final Rag status;
    private final String target;         // nullable, e.g. "Target >=90%"
}
