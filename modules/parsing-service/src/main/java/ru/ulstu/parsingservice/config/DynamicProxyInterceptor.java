package ru.ulstu.parsingservice.config;

import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class DynamicProxyInterceptor implements ClientHttpRequestInterceptor {

    private final UserAgentProvider userAgentProvider;
    private final ProxyProvider proxyProvider;

    public DynamicProxyInterceptor(UserAgentProvider userAgentProvider, ProxyProvider proxyProvider) {
        this.userAgentProvider = userAgentProvider;
        this.proxyProvider = proxyProvider;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Получаем случайный прокси
        InetSocketAddress proxyAddress = proxyProvider.getRandomProxy();
        log.info("Используемый прокси: {}:{}", proxyAddress.getHostName(), proxyAddress.getPort());

        // Устанавливаем прокси
        System.setProperty("http.proxyHost", proxyAddress.getHostName());
        System.setProperty("http.proxyPort", String.valueOf(proxyAddress.getPort()));

        // Устанавливаем динамический user-agent
        String randomUserAgent = userAgentProvider.getRandomUserAgent();
        log.info("Используемый User-Agent: {}", randomUserAgent);
        request.getHeaders().set("User-Agent", randomUserAgent);

        // Выполняем запрос
        return execution.execute(request, body);
    }
}
