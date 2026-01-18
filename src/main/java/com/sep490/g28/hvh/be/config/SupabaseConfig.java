package com.sep490.g28.hvh.be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "supabase")
@Getter
@Setter
public class SupabaseConfig {
    private String url;
    private String apiSecretKey; // aka service role key/ service key...
    private String bucket;
}
