package ru.ulstu.parsingservice.config;

import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Slf4j
@Configuration
@AllArgsConstructor
public class WebClientConfig {
    private final UserAgentProvider userAgentProvider;
    private final ru.ulstu.parsingservice.config.ProxyProvider proxyProvider;


    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter((request, next) -> {
                    // Получаем случайный прокси для каждого запроса
                    InetSocketAddress proxyAddress = proxyProvider.getRandomProxy();
                    log.info("Используемый прокси: {}:{}", proxyAddress.getHostName(), proxyAddress.getPort());

                    HttpClient httpClient = HttpClient.create()
                            .proxy(proxy -> proxy
                                    .type(ProxyProvider.Proxy.HTTP)
                                    .address(proxyAddress));

                    String randomUserAgent = userAgentProvider.getRandomUserAgent();
                    log.info("Используемый User-Agent: {}", randomUserAgent);

                    // Создаем новый WebClient с прокси
                    WebClient webClientWithProxy = WebClient.builder()
                            .clientConnector(new ReactorClientHttpConnector(httpClient))
                            .build();

                    // Выполняем запрос с обновленным User-Agent через WebClient с прокси
                    return webClientWithProxy
                            .method(request.method())
                            .uri(request.url())
                            .headers(headers -> headers.putAll(request.headers()))
                            .header(HttpHeaders.USER_AGENT, randomUserAgent)
                            .body(request.body()).exchange();
                })
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }
}

