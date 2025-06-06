package com.tiltedhat.bg_remove_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BgRemoveApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BgRemoveApiApplication.class, args);
	}

}
