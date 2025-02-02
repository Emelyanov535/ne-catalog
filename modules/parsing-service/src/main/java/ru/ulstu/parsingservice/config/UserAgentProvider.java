package ru.ulstu.parsingservice.config;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class UserAgentProvider {
    private static final List<String> userAgents = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15A372 Safari/604.1"
    );

    public String getRandomUserAgent() {
        return userAgents.get(new Random().nextInt(userAgents.size()));
    }
}
