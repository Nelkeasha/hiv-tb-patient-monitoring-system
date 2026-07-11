package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.report.KvSection;
import com.nelly.hivtbmonitoringsystem.dto.report.LineListing;
import com.nelly.hivtbmonitoringsystem.dto.report.ReportModel;
import com.nelly.hivtbmonitoringsystem.service.export.support.PdfReportBuilder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Renders a {@link ReportModel} as a formal management-report PDF: a headline
 * KPI strip with period-over-period deltas, a generated executive-summary
 * narrative, ranked recommendations, supporting breakdowns, and named case
 * line-listings — on the shared DMC letterhead.
 */
@Service
public class SupervisorPdfReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generate(ReportModel model) {
        StringBuilder meta = new StringBuilder();
        meta.append(model.getScopeName() != null ? model.getScopeName() : "Facility");
        if (model.getSubScope() != null && !model.getSubScope().isBlank()) {
            meta.append(" - ").append(model.getSubScope());
        }
        meta.append("   |   Period: ").append(model.getPeriodLabel());
        meta.append("   |   ").append(model.getComparisonLabel());
        meta.append("   |   Generated: ")
            .append(model.getGeneratedAt() != null ? model.getGeneratedAt().format(TS) : "-");

        PdfReportBuilder builder = new PdfReportBuilder()
                .header(model.getReportTitle(), meta.toString())
                .kpiCards("Programme Indicators", model.getIndicators())
                .executiveSummary(model.getExecutiveSummary())
                .recommendations(model.getRecommendations());

        if (model.getKvSections() != null) {
            for (KvSection s : model.getKvSections()) {
                builder.section(s.getTitle(), s.getRows());
            }
        }

        if (model.getLineListings() != null) {
            for (LineListing l : model.getLineListings()) {
                builder.dataTable(l.getTitle(), l.getHeaders(), l.getRows(), l.getEmptyMessage());
            }
        }

        builder.signOff(model.getGeneratedBy(), null)
               .footer("This is a system-generated report from the HIV/TB Monitoring System. "
                       + "Figures reflect data as of the generation timestamp above. "
                       + "Patients are identified by code to preserve confidentiality.");

        return builder.build();
    }
}
