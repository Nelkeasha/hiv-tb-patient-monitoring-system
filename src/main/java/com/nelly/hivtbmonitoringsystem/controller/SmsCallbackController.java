package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.service.SmsConfirmationService;
import com.nelly.hivtbmonitoringsystem.service.SmsConfirmationService.SmsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Africa's Talking SMS callback endpoint.
 *
 * AT posts application/x-www-form-urlencoded with fields:
 *   from  — sender phone e.g. "+250788000006"
 *   text  — SMS body e.g. "YES"
 *   to    — our short code
 *   date  — delivery timestamp (not used; server time is used for confirmation)
 *
 * This endpoint is intentionally PUBLIC (no JWT) because it is called
 * by Africa's Talking servers, not by app users.
 */
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Slf4j
public class SmsCallbackController {

    private final SmsConfirmationService smsConfirmationService;

    @PostMapping(value = "/callback", consumes = {
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<Map<String, String>> callback(
            @RequestParam(value = "from",  required = false) String from,
            @RequestParam(value = "text",  required = false) String text,
            @RequestParam(value = "to",    required = false) String to,
            @RequestParam(value = "date",  required = false) String date) {

        log.info("SMS callback received: from={} text='{}' to={} date={}", from, text, to, date);

        if (from == null || from.isBlank()) {
            return ResponseEntity.ok(Map.of("status", "ignored", "reason", "missing from"));
        }

        SmsResult result = smsConfirmationService.process(from, text);

        String replyHint = switch (result) {
            case CONFIRMED        -> "Thank you — your dose has been recorded.";
            case MISSED           -> "Missed dose recorded. Please take your medication as soon as possible.";
            case UNRECOGNIZED     -> "Reply YES if you took your medication today, or NO if you did not.";
            case PATIENT_NOT_FOUND -> null;
            case NO_ACTIVE_SCHEDULE -> null;
            case ALREADY_CONFIRMED -> "Your dose was already confirmed today.";
        };

        Map<String, String> body = replyHint != null
                ? Map.of("status", result.name(), "reply", replyHint)
                : Map.of("status", result.name());

        return ResponseEntity.ok(body);
    }
}
