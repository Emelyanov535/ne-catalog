package ru.necatalog.wildberriesparser.config;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class ProxyProvider {
    private static final List<String> proxies = List.of(
            "85.215.64.49:80",
            "82.115.19.142:80",
            "148.113.172.51:8080"
    );

    public InetSocketAddress getRandomProxy() {
        String[] proxy = proxies.get(new Random().nextInt(proxies.size())).split(":");
        return new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]));
    }
}
