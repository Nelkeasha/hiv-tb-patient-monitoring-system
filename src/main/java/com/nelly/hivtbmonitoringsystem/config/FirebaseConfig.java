package com.nelly.hivtbmonitoringsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Initializes Firebase Admin SDK on startup.
 *
 * Configuration via Render environment variable:
 *   FIREBASE_SERVICE_ACCOUNT_JSON — full service account JSON string
 *
 * If the variable is absent or empty, Firebase is disabled silently.
 * All FcmService sends become no-ops. The app runs normally without FCM.
 *
 * To enable:
 *   1. Create a Firebase project at console.firebase.google.com
 *   2. Project settings → Service accounts → Generate new private key
 *   3. Paste the JSON content into Render env var FIREBASE_SERVICE_ACCOUNT_JSON
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-json:}")
    private String serviceAccountJson;

    @PostConstruct
    public void initialize() {
        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            log.info("Firebase: FIREBASE_SERVICE_ACCOUNT_JSON not set — FCM push notifications disabled");
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            return; // already initialized (e.g. hot reload in dev)
        }
        try {
            InputStream stream = new ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8));
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialized — FCM push notifications enabled");
        } catch (Exception e) {
            log.warn("Firebase initialization failed — FCM disabled: {}", e.getMessage());
        }
    }
}
