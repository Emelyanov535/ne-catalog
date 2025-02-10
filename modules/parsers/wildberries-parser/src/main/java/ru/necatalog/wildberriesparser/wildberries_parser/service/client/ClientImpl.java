package ru.necatalog.wildberriesparser.wildberries_parser.service.client;

import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;

@AllArgsConstructor
@Service
@Slf4j
public class ClientImpl implements Client {

    private final RestTemplate restTemplate;
    private final WildberriesConfigProperties wildberriesConfigProperties;


    @Override
    @Retryable(maxAttempts = 50, value = RuntimeException.class)
    public Map<String, Object> scrapPage(int page, String shard, String query) {
        String url = wildberriesConfigProperties.getCatalogWbUrl() +
                shard +
                query +
                "?dest=-1257786&page=" + page + "&subject=2290";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody();
    }

    @Recover
    public Map<String, Object> recover(RuntimeException e, int page, String shard, String query) {
        // Логика обработки неудачи после всех попыток
        log.error("Все попытки завершились неудачей: {}", e.getMessage());
        // Можно вернуть пустую карту или другое значение по умолчанию
        return Collections.emptyMap();
    }


//    @Override
//    public Map<String, Object> scrapPage(int page, String shard, String query) {
//        String url = marketplacesConfig.getWildberriesConfigProperties().getCatalogWbUrl() +
//                shard +
//                query +
//                "?dest=-1257786&page=" + page + "&subject=2290";
//
//        try {
//            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000) + 500);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        return webClient.get()
//                .uri(url)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                })
//                .retry(50)
//                .block();
//    }
}
