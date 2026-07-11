package com.nelly.hivtbmonitoringsystem.dto.report;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The tier-agnostic shape of a management report. One assembler per report tier
 * (Supervisor, Facility, Admin) populates this model; every renderer
 * (PDF, Excel, CSV, web) consumes this same model. That is what turns a report
 * from "a wall of counts" into a narrative document, and what lets the three
 * tiers share one presentation layer.
 */
@Getter
@Builder
public class ReportModel {

    // ── Header / cover ────────────────────────────────────────────────────────
    private final String reportTitle;   // e.g. "Supervisor Programme Report"
    private final String scopeName;     // e.g. facility name
    private final String subScope;      // e.g. district (nullable)
    private final String periodLabel;   // e.g. "01 Jun – 30 Jun 2026"
    private final String comparisonLabel; // e.g. "vs previous 30 days (02 May – 31 May)"
    private final LocalDateTime generatedAt;
    private final String generatedBy;   // preparer name

    // ── Body ──────────────────────────────────────────────────────────────────
    /**
     * Executive-summary paragraphs. Each entry is "lead||body"; the renderer
     * bolds the lead-in (e.g. "What happened.") and justifies the body.
     */
    private final List<String> executiveSummary;

    /** Headline KPI cards. */
    private final List<Indicator> indicators;

    /** Supporting key/value breakdowns (patient mix, risk distribution, ...). */
    private final List<KvSection> kvSections;

    /** Named case lists — the "who is affected" detail. */
    private final List<LineListing> lineListings;

    /** Ranked, machine-generated actions. */
    private final List<Recommendation> recommendations;
}
