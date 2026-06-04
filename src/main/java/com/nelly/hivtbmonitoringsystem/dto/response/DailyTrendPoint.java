package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class DailyTrendPoint {
    private String day;        // "Mon", "Tue", …
    private int adherence;     // 0-100 percentage
    private int confirmed;     // 0-100 percentage (patient-confirmed within window)
    private int visits;        // home visits (supervisor chart only)
    private int missed;        // missed doses (supervisor chart only)
}
