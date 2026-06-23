package com.nelly.hivtbmonitoringsystem.service.export.support;

import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * The three export formats every report supports, so the frontend can offer
 * one dropdown ("PDF / Excel / CSV") instead of each role being locked to a
 * single fixed format.
 */
public enum ReportFormat {
    PDF("pdf", MediaType.APPLICATION_PDF),
    EXCEL("xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    CSV("csv", MediaType.parseMediaType("text/csv"));

    public final String fileExtension;
    public final MediaType contentType;

    ReportFormat(String fileExtension, MediaType contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public static ReportFormat fromParam(String format) {
        if (format == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'format' query parameter (pdf, excel, or csv)");
        }
        switch (format.toLowerCase()) {
            case "pdf": return PDF;
            case "excel": case "xlsx": return EXCEL;
            case "csv": return CSV;
            default: throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported format '" + format + "' — must be pdf, excel, or csv");
        }
    }
}
