package com.sep490.g28.hvh.be.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    //use to send json request
    @Bean
    @Qualifier("supabaseRestTemplate")
    public RestTemplate supabaseRestTemplate(SupabaseConfig config) {
        RestTemplate rt = new RestTemplate();

        rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(config.getApiSecretKey());
            request.getHeaders().set("apikey", config.getApiSecretKey());
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        });

        return rt;
    }
}
