package ru.necatalog.wildberriesparser.service.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;
import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;
import ru.necatalog.wildberriesparser.service.dto.ProductListDto;
import ru.necatalog.wildberriesparser.util.CaptchaTokenProvider;

import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class ClientImpl implements Client {

	private final WildberriesConfigProperties wildberriesConfigProperties;
	private final RestTemplate restTemplateScrapping;
	private final CaptchaTokenProvider captchaTokenProvider;

	@Override
	@SneakyThrows
	@Retryable(value = {HttpClientErrorException.TooManyRequests.class}, maxAttempts = 21)
	public ProductListDto scrapPage(int page) {
		String url = wildberriesConfigProperties.buildLaptopCategoryUrl(page);
		log.info("Requesting URL: {}", url);

		HttpHeaders headers = createCommonHeaders();
		headers.add("X-captcha-id", captchaTokenProvider.getCurrentCaptchaId());

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			return restTemplateScrapping.exchange(
					url,
					HttpMethod.GET,
					entity,
					new ParameterizedTypeReference<ProductListDto>() {
					}
			).getBody();
		} catch (HttpClientErrorException.TooManyRequests e) {
			log.warn("Request failed (429 Too Many Requests)");
			captchaTokenProvider.requestFailed();
			throw e;
		}
	}

	@Override
	@SneakyThrows
	public ProductAttributesResponse scrapAttributes(String url) {
		log.info("Requesting URL: {}", url);

		RestTemplate restTemplate = new RestTemplate();

		try {
			ResponseEntity<ProductAttributesResponse> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					HttpEntity.EMPTY,
					new ParameterizedTypeReference<>() {
					}
			);

			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			} else {
				log.warn("Failed to fetch attributes for URL: {}. Status code: {}", url, response.getStatusCode());
				return null;
			}
		} catch (HttpClientErrorException e) {
			log.warn("Client error when fetching URL {}: {} {}", url, e.getStatusCode(), e.getStatusText());
		} catch (HttpServerErrorException e) {
			log.warn("Server error when fetching URL {}: {} {}", url, e.getStatusCode(), e.getStatusText());
		} catch (ResourceAccessException e) {
			log.warn("Connection error when fetching URL {}: {}", url, e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error when fetching URL {}: {}", url, e.getMessage(), e);
		}

		return null;
	}



	private HttpHeaders createCommonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "*/*");
		headers.add("Accept-Encoding", "gzip, deflate, br, zstd");
		headers.add("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
		headers.add("Origin", "https://www.wildberries.ru");
		headers.add("Priority", "u=1, i");
		headers.add("Referer", "https://www.wildberries.ru/catalog/elektronika/noutbuki-pereferiya/noutbuki-ultrabuki");
		headers.add("Sec-Ch-Ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
		headers.add("Sec-Ch-Ua-Mobile", "?0");
		headers.add("Sec-Ch-Ua-Platform", "\"Windows\"");
		headers.add("Sec-Fetch-Dest", "empty");
		headers.add("Sec-Fetch-Mode", "cors");
		headers.add("Sec-Fetch-Site", "cross-site");
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
		return headers;
	}
}
