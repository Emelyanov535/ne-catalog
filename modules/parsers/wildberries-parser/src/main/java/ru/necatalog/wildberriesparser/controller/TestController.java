package ru.necatalog.wildberriesparser.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

	@GetMapping("/test")
	public List<ProductSpecification> test() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		String responseEntity = restTemplate.exchange(
				"https://basket-17.wbbasket.ru/vol2707/part270762/270762735/info/ru/card.json",
				HttpMethod.GET,
				HttpEntity.EMPTY,
				new ParameterizedTypeReference<String>() {
				}
		).getBody();

		List<ProductSpecification> productSpecifications = extractOptions(responseEntity);
		return productSpecifications;
	}

	public List<ProductSpecification> extractOptions(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		// Преобразуем основной JSON в объект JsonNode
		JsonNode rootNode = objectMapper.readTree(json);

		// Извлекаем массив "options"
		JsonNode optionsNode = rootNode.path("options");

		// Преобразуем массив "options" в список объектов ProductSpecification
		List<ProductSpecification> productSpecifications = new ArrayList<>();
		for (JsonNode option : optionsNode) {
			String name = option.path("name").asText();
			String value = option.path("value").asText();
			productSpecifications.add(new ProductSpecification(name, value));
		}

		return productSpecifications;
	}

	public List<ProductSpecification> parseJson(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ProductSpecification.class));
	}


}
