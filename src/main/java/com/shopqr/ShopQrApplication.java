package com.shopqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class ShopQrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopQrApplication.class, args);
	}

	@GetMapping("/api/status")
	public Map<String, String> status() {
		return Map.of(
			"status", "UP",
			"message", "Shop QR Backend is running correctly.",
			"version", "0.0.1"
		);
	}
}
