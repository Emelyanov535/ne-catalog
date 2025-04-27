package ru.necatalog.wildberriesparser.config;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class ProxyProvider {
    private static final List<String> proxies = List.of(
            "194.32.251.113:8000:vHCfY5:au1gsa"
    );

    private final Random random = new Random();

    public ProxyConfig getRandomProxy() {
        if (random.nextDouble() < 0.7) { // 70% запросов идут через прокси, 30% без
            String[] proxyData = proxies.get(random.nextInt(proxies.size())).split(":");
            return new ProxyConfig(proxyData[0], Integer.parseInt(proxyData[1]), proxyData[2], proxyData[3]);
        }
        return null;
    }

    public record ProxyConfig(String host, int port, String username, String password) {
    }
}
