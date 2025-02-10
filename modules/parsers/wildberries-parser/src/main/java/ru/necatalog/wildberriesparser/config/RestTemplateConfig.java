package ru.necatalog.wildberriesparser.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class RestTemplateConfig {

    private final UserAgentProvider userAgentProvider;
    private final ProxyProvider proxyProvider;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestInterceptor dynamicProxyInterceptor = new DynamicProxyInterceptor(userAgentProvider, proxyProvider);

        // Добавляем интерсептор в RestTemplate
        //restTemplate.setInterceptors(Collections.singletonList(dynamicProxyInterceptor));

        return restTemplate;
    }

}
