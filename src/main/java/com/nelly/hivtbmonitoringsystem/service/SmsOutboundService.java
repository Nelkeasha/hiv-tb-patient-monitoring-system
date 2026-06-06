package com.nelly.hivtbmonitoringsystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SmsOutboundService {

    private static final String AT_SMS_URL = "https://api.africastalking.com/version1/messaging";

    @Value("${app.sms.enabled:false}")
    private boolean enabled;

    @Value("${app.sms.api-key:}")
    private String apiKey;

    @Value("${app.sms.username:sandbox}")
    private String username;

    @Value("${app.sms.sender-id:HIVTB}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();

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

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("username", username);
            body.add("to", toPhone);
            body.add("message", message);
            body.add("from", senderId);

            ResponseEntity<String> response = restTemplate.exchange(
                    AT_SMS_URL, HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

            log.info("SMS sent to {}: status={}", toPhone, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhone, e.getMessage());
        }
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
