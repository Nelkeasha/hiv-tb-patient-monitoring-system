package com.nelly.hivtbmonitoringsystem.dto.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * A named case list — the "who is affected" detail of a management report
 * (e.g. patients below the adherence threshold, high-risk patients, unresolved
 * critical alerts). Patients are identified by patient code, never by name, to
 * preserve confidentiality in an exported/printed document.
 */
@Getter
@Builder
public class LineListing {
    private final String title;
    private final String[] headers;
    private final List<String[]> rows;
    private final String emptyMessage;
}
