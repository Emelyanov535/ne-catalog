package ru.necatalog.wildberriesparser.wildberries_parser.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.PriceHistoryId;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;
import ru.necatalog.wildberriesparser.wildberries_parser.service.client.Client;
import ru.necatalog.wildberriesparser.wildberries_parser.service.dto.ProductInfoDto;

@Service("wildberriesParsingService")
@AllArgsConstructor
public class ParsingService {
    private final Client client;
    private final ObjectMapper objectMapper;
    private final ConversionService conversionService;
    private final WildberriesConfigProperties wildberriesConfigProperties;
    private final ProductService productService;

    public void parse() {

        final int elementsInPage = 100;
        int page = 1;
        Integer totalPages = null;

        do {
            var pageData = client.scrapPage(page, wildberriesConfigProperties.getShard(), wildberriesConfigProperties.getLaptopUrl());
            System.out.println("Получена страница: " + page);
            if (totalPages == null) {
                Map<String, Object> dataMap = (Map<String, Object>) pageData.get("data");
                int totalElements = (int) dataMap.get("total");
                totalPages = (int) Math.ceil((double) totalElements / elementsInPage);
            }

            List<ProductEntity> productEntities = new ArrayList<>();
            List<PriceHistoryEntity> priceHistories = new ArrayList<>();
            List<ProductInfoDto> productInfoDtoList = convertMapObjectToListProductInfoDto(pageData);

            productInfoDtoList.forEach(dto -> {

                ProductEntity productEntity = conversionService.convert(dto, ProductEntity.class);
                productEntity.setUrl("https://www.wildberries.ru/catalog/" + dto.getId() + "/detail.aspx?targetUrl=BP");

                PriceHistoryEntity priceHistory = PriceHistoryEntity.builder()
                        .id(new PriceHistoryId(productEntity.getUrl(), ZonedDateTime.now()))
                        .price(BigDecimal.valueOf(dto.getSalePriceU() / 100.0))
                        .build();

                productEntities.add(productEntity);
                priceHistories.add(priceHistory);
            });
            productService.saveData(productEntities, priceHistories);
            page++;
//        } while (page <= totalPages);
        } while (page <= 5);
    }

    private List<ProductInfoDto> convertMapObjectToListProductInfoDto(Map<String, Object> map) {
        Map<String, ArrayList<Object>> dataMap = (Map<String, ArrayList<Object>>) map.get("data");
        return getProductInfoDtos(dataMap);
    }

    private List<ProductInfoDto> getProductInfoDtos(Map<String, ArrayList<Object>> dataMap) {
        return objectMapper.convertValue(
                dataMap.get("products"),
                new TypeReference<>() {
                }
        );
    }
}
