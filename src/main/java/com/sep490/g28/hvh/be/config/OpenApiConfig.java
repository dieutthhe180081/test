package com.sep490.g28.hvh.be.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
//@Profile("dev")
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OpenApi specification - HVH")
                        .description("OpenApi documentation for Hanoi Volunteer Hub")
                        .version("1"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local ENV")
                ));

    }
}
