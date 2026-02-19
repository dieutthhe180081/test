package com.sep490.g28.hvh.be;

import com.sep490.g28.hvh.be.config.RabbitMqEmailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableConfigurationProperties(RabbitMqEmailProperties.class)
@EnableSpringDataWebSupport(
		pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class HvhBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HvhBeApplication.class, args);
	}

}
