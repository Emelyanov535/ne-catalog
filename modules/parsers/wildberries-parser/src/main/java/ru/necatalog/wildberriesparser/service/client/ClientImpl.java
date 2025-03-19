package ru.necatalog.wildberriesparser.service.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;

import java.util.Map;
import java.util.Random;

import static ru.necatalog.wildberriesparser.config.RestTemplateFactory.createRestTemplateWithDynamicProxy;

@AllArgsConstructor
@Service
@Slf4j
public class ClientImpl implements Client {

    private final WildberriesConfigProperties wildberriesConfigProperties;
    private final Random random = new Random();

    @Override
    @Retryable(maxAttempts = 50, value = RuntimeException.class)
    public Map<String, Object> scrapPage(int page, String shard, String query) {
        String url = wildberriesConfigProperties.getCatalogWbUrl() +
                shard +
                query +
                "?dest=-1257786&page=" + page + "&subject=2290";

        System.out.println(url);
        int randomDelay = 1000 + random.nextInt(2000);
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка при задержке запроса", e);
        }

        RestTemplate restTemplate = createRestTemplateWithDynamicProxy();

        String ip = restTemplate.getForObject("http://checkip.amazonaws.com", String.class);
		log.info("Current Tor IP: {}", ip);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        ).getBody();
    }
}
