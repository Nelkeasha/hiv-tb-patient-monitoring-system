package com.nelly.hivtbmonitoringsystem.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * Pins the JVM default time zone to the clinic's local zone (Rwanda / CAT,
 * Africa/Kigali by default).
 *
 * <p>Why this matters: hosted runtimes (e.g. Render) default to UTC. Dose times
 * are entered by clinical staff as local wall-clock times (e.g. 08:00 for the
 * morning dose), and the reminder + missed-dose schedulers compare them against
 * {@code LocalTime.now()}. Without this, a UTC server fires the 08:00 reminder
 * at 10:00 local, and report day-bucketing shifts around midnight. Setting one
 * app-wide zone keeps reminders, missed-dose detection, and report dates all on
 * clinic-local time.
 *
 * <p>Override with {@code APP_CLINIC_ZONE} / {@code app.clinic-zone} if the
 * deployment serves a different region.
 */
@Configuration
@Slf4j
public class TimeZoneConfig {

    @Value("${app.clinic-zone:Africa/Kigali}")
    private String clinicZone;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(clinicZone));
        log.info("Application default time zone set to {} (now: {})",
                clinicZone, java.time.ZonedDateTime.now());
    }
}
