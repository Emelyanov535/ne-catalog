package ru.necatalog.wildberriesparser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.PriceHistoryId;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;
import ru.necatalog.wildberriesparser.service.client.Client;
import ru.necatalog.wildberriesparser.service.dto.ProductInfoDto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("wildberriesParsingService")
@AllArgsConstructor
public class ParsingService {
    private static final int ELEMENTS_IN_PAGE = 100;
    private static final String URL_FORMAT = "https://www.wildberries.ru/catalog/%d/detail.aspx?targetUrl=BP";
    private static final String IMAGE_URL_FORMAT = "https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp";

    private final Client client;
    private final ObjectMapper objectMapper;
    private final ConversionService conversionService;
    private final WildberriesConfigProperties wildberriesConfigProperties;
    private final ProductService productService;
    private final TestService testService;

    public void parse() {
        int page = 1;
        Integer totalPages = null;

        do {
            var pageData = client.scrapPage(page, wildberriesConfigProperties.getShard(), wildberriesConfigProperties.getLaptopUrl());
            if (pageData == null) {
                continue;
            }

			log.info("Получена страница: {}", page);

            if (totalPages == null) {
                totalPages = calculateTotalPages(pageData);
            }

            List<ProductEntity> productEntities = new ArrayList<>();
            List<PriceHistoryEntity> priceHistories = new ArrayList<>();
            List<ProductInfoDto> productInfoDtoList = convertMapObjectToListProductInfoDto(pageData);

            productInfoDtoList.forEach(dto -> {

                ProductEntity productEntity = conversionService.convert(dto, ProductEntity.class);
                productEntity.setUrl(String.format(URL_FORMAT, dto.getId()));
                productEntity.setImageUrl(getImageUrl(dto.getId()));

                PriceHistoryEntity priceHistory = PriceHistoryEntity.builder()
                        .id(new PriceHistoryId(productEntity.getUrl(), ZonedDateTime.now()))
                        .price(BigDecimal.valueOf(dto.getSizes().getFirst().getPrice().getTotal() / 100.0))
                        .build();

                productEntities.add(productEntity);
                priceHistories.add(priceHistory);
            });
            testService.checkPriceAndNotify(productEntities, priceHistories);
            productService.saveData(productEntities, priceHistories);
            // Здесь логика по просмотру изменилась ли цена товара с предыдущими ценами, отправка уведомлений
            page++;
//        } while (page <= totalPages);
        } while (page <= 100);
    }

    private Integer calculateTotalPages(Map<String, Object> pageData) {
        Map<String, Object> dataMap = (Map<String, Object>) pageData.get("data");
        int totalElements = (int) dataMap.get("total");
        return (int) Math.ceil((double) totalElements / ELEMENTS_IN_PAGE);
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

    public static String getImageUrl(Long productId) {
        long vol = productId / 100000;
        Long part = productId / 1000;
        String basket = getBasket(vol);

        return String.format(IMAGE_URL_FORMAT, basket, vol, part, productId);
    }

    private static String getBasket(long vol) {
        if (vol <= 143) return "01";
        if (vol <= 287) return "02";
        if (vol <= 431) return "03";
        if (vol <= 719) return "04";
        if (vol <= 1007) return "05";
        if (vol <= 1061) return "06";
        if (vol <= 1115) return "07";
        if (vol <= 1169) return "08";
        if (vol <= 1313) return "09";
        if (vol <= 1601) return "10";
        if (vol <= 1655) return "11";
        if (vol <= 1919) return "12";
        if (vol <= 2045) return "13";
        return "14";
    }
}
