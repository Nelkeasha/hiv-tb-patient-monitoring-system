package com.nelly.hivtbmonitoringsystem.service.export.support;

/**
 * Shared RFC 4180 field escaping for every flat-CSV report export, with
 * OWASP CSV-injection protection (formula-prefix neutralization).
 */
public final class CsvUtil {

    private CsvUtil() {}

    public static String escape(String value) {
        if (value == null) return "";
        String safe = neutralizeFormula(value);
        boolean needsQuoting = safe.contains(",") || safe.contains("\"") || safe.contains("\n");
        return needsQuoting ? "\"" + safe.replace("\"", "\"\"") + "\"" : safe;
    }

    /**
     * Prefixes values that start with =, +, -, or @ with a single quote so spreadsheet
     * apps (Excel, Sheets) display them as text instead of executing them as formulas.
     * See OWASP CSV Injection guidance.
     */
    private static String neutralizeFormula(String value) {
        if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
            return "'" + value;
        }
        return value;
    }
}
