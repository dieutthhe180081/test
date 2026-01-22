package com.sep490.g28.hvh.be;

import com.sep490.g28.hvh.be.config.RabbitMqMailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RabbitMqMailProperties.class)
public class HvhBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HvhBeApplication.class, args);
	}

}
