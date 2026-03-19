package com.sep490.g28.hvh.be.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * Firebase configuration for initializing the Firebase Admin SDK.
 *
 * <p>This configuration loads the Firebase service account credentials from
 * {@code serviceAccountKey.json} located on the classpath and initializes
 * a {@link FirebaseApp} instance during application startup.</p>
 *
 * <p>The initialization is executed in {@link PostConstruct} to ensure that
 * Firebase is ready before any Firebase-dependent components are used.</p>
 *
 * <p>To avoid duplicate initialization errors, the Firebase app is only
 * initialized if no existing {@link FirebaseApp} instances are present.</p>
 *
 * @see FirebaseApp
 * @see FirebaseOptions
 * @see GoogleCredentials
 */
@Configuration
public class FirebaseConfig {
    @Value("${firebase.credentials-path}")
    private String configPath;
    @PostConstruct
    void init() throws Exception {
        InputStream serviceAccount =
                new ClassPathResource(configPath).getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

}
