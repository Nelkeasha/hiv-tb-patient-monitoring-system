package com.nelly.hivtbmonitoringsystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsOutboundService {

    private static final String AT_SMS_URL         = "https://api.africastalking.com/version1/messaging";
    private static final String AT_SMS_SANDBOX_URL  = "https://api.sandbox.africastalking.com/version1/messaging";

    @Value("${app.sms.enabled:false}")
    private boolean enabled;

    @Value("${app.sms.api-key:}")
    private String apiKey;

    @Value("${app.sms.username:sandbox}")
    private String username;

    @Value("${app.sms.sender-id:HIVTB}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();

    /** Sandbox doesn't accept custom sender IDs/shortcodes unless registered, so omit "from" there. */
    private MultiValueMap<String, String> buildBody(String toPhone, String message) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("to", toPhone);
        body.add("message", message);
        if (!"sandbox".equalsIgnoreCase(username)) {
            body.add("from", senderId);
        }
        return body;
    }

    @Async
    public void send(String toPhone, String message) {
        if (!enabled) {
            log.info("SMS disabled — would have sent to {}: {}", toPhone, message);
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("apiKey", apiKey);
            headers.set("Accept", "application/json");

            MultiValueMap<String, String> body = buildBody(toPhone, message);

            String url = "sandbox".equalsIgnoreCase(username) ? AT_SMS_SANDBOX_URL : AT_SMS_URL;
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

            log.info("SMS sent to {}: status={}", toPhone, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhone, e.getMessage());
        }
    }

    /** Config status for diagnostics — never exposes the actual API key. */
    public Map<String, Object> getConfigStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("enabled", enabled);
        status.put("apiKeyConfigured", apiKey != null && !apiKey.isBlank());
        status.put("username", username);
        status.put("senderId", senderId);
        status.put("endpoint", "sandbox".equalsIgnoreCase(username) ? AT_SMS_SANDBOX_URL : AT_SMS_URL);
        return status;
    }

    /** Synchronous send for diagnostics — returns the AT response or error instead of just logging it. */
    public Map<String, Object> sendSync(String toPhone, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!enabled) {
            result.put("status", "skipped");
            result.put("reason", "SMS disabled (app.sms.enabled=false)");
            return result;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("apiKey", apiKey);
            headers.set("Accept", "application/json");

            MultiValueMap<String, String> body = buildBody(toPhone, message);

            String url = "sandbox".equalsIgnoreCase(username) ? AT_SMS_SANDBOX_URL : AT_SMS_URL;
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

            result.put("status", "sent");
            result.put("httpStatus", response.getStatusCode().value());
            result.put("body", response.getBody());
        } catch (HttpClientErrorException e) {
            result.put("status", "error");
            result.put("httpStatus", e.getStatusCode().value());
            result.put("body", e.getResponseBodyAsString());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Async
    public void sendPatientWelcome(String phone, String fullName, String loginEmail, String tempPassword) {
        String message = "Dear " + fullName + ", your HIV/TB Monitor account is ready.\n" +
                "Login: " + loginEmail + "\n" +
                "Password: " + tempPassword + "\n" +
                "Change it on first login. Ask your CHW for the app.\n" +
                "- Dream Medical Center";
        send(phone, message);
    }
}
