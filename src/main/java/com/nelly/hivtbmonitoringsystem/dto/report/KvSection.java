package com.nelly.hivtbmonitoringsystem.dto.report;

import lombok.Builder;
import lombok.Getter;

/**
 * A small key/value detail block (e.g. patient mix, risk distribution). Kept
 * for the supporting breakdowns that sit below the headline KPIs.
 */
@Getter
@Builder
public class KvSection {
    private final String title;
    /** Each row is {label, value}. */
    private final String[][] rows;
}
