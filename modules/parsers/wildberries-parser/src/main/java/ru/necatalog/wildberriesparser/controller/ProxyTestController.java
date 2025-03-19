package ru.necatalog.wildberriesparser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProxyTestController {

//	private final RestTemplate restTemplate;
//
//	@GetMapping("/check-ip")
//	public String checkIp() {
//		String ip = restTemplate.getForObject("http://checkip.amazonaws.com", String.class);
//		return "Current Tor IP: " + ip;
//	}
}
