package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.AlertResponse;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Forwards each dose confirmation to the Python AI microservice's
 * /confirmation/analyze endpoint, which compares the response time against the
 * patient's personal baseline, checks the dose window, and cross-references the
 * most recent CHW pill count. Runs off the request thread — the AI service can
 * be cold (Render free tier) and must never delay the patient's "confirmed" response.
 *
 * Takes plain values rather than JPA entities: this method runs in a thread with
 * no Hibernate session, so any unfetched lazy association (e.g. Patient.chw)
 * would throw LazyInitializationException if accessed here.
 */
@Service
@Slf4j
public class AiConfirmationAnalysisService {

    @Value("${ai.service.base-url}")
    private String aiServiceBaseUrl;

    @Value("${ai.service.api-key}")
    private String aiServiceApiKey;

    private final AlertService alertService;
    private final RestTemplate restTemplate;

    public AiConfirmationAnalysisService(AlertService alertService) {
        this.alertService = alertService;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(60_000); // tolerate Render free-tier cold start
        this.restTemplate = new RestTemplate(factory);
    }

    @Async
    public void analyze(UUID confirmationLogId, UUID patientId, String patientName, UUID chwId,
                         UUID scheduleId, Integer responseTimeSeconds, LocalDateTime confirmedAt,
                         LocalDateTime windowOpenTime, LocalDateTime windowCloseTime) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-API-Key", aiServiceApiKey);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("patient_id", patientId);
            body.put("schedule_id", scheduleId);
            body.put("response_time_seconds", responseTimeSeconds);
            body.put("confirmed_at", confirmedAt);
            body.put("window_open_time", windowOpenTime);
            body.put("window_close_time", windowCloseTime);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>)
                    restTemplate.postForEntity(
                            aiServiceBaseUrl + "/confirmation/analyze",
                            new HttpEntity<>(body, headers),
                            Map.class);

            Map<String, Object> result = response.getBody();
            if (result == null) return;

            String alertId = (String) result.get("alert_id");
            if (alertId == null) {
                return; // AI did not flag this confirmation as suspicious
            }

            relayAlert(patientId, patientName, chwId, alertId, result);
        } catch (Exception e) {
            log.warn("AI confirmation analysis unavailable for log {}: {}", confirmationLogId, e.getMessage());
        }
    }

    private void relayAlert(UUID patientId, String patientName, UUID chwId, String alertId, Map<String, Object> result) {
        AlertResponse alertResponse = AlertResponse.builder()
                .id(UUID.fromString(alertId))
                .patientId(patientId)
                .patientName(patientName)
                .chwId(chwId)
                .alertType(AlertType.FALSE_CONFIRMATION)
                .severity(AlertSeverity.WARNING)
                .title((String) result.getOrDefault("alert_title", "Suspicious Dose Confirmation Detected"))
                .message((String) result.getOrDefault("alert_message",
                        String.join("; ", castSignals(result.get("signals_triggered")))))
                .isRead(false)
                .isResolved(false)
                .createdAt(LocalDateTime.now())
                .build();

        alertService.broadcastExternalAlert(alertResponse);
    }

    @SuppressWarnings("unchecked")
    private List<String> castSignals(Object signals) {
        return signals instanceof List<?> list ? (List<String>) list : List.of();
    }
}
